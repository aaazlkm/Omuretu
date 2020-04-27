package omuretu.ast.postfix

import omuretu.environment.base.VariableEnvironment
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function.OmuretuFunction
import omuretu.model.Function.NativeFunction
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.HeapMemory
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.CallOpecode
import omuretu.vertualmachine.opecode.MoveOpecode
import parser.ast.ASTTree

class ArgumentPostfix(
        private val astTrees: List<ASTTree>
) : Postfix(astTrees) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_ARGUMENT_BREAK = ","

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ArgumentPostfix(argument)
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment, leftType: Type): Type {
        val functionType = leftType as? Type.Defined.Function ?: throw OmuretuException("bad left type", this)
        if (astTrees.size != functionType.parameterTypes.size) throw OmuretuException("bad number of argument", this)
        functionType.parameterTypes
                .zip(astTrees.map { it.checkType(typeEnvironment) })
                .forEach { TypeCheckHelper.checkSubTypeOrThrow(it.first, it.second, this, typeEnvironment) }
        return functionType.returnType
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        // defStmntのcompileメソッドでstackFrameSizeに格納している
        var offset = byteCodeStore.stackFrameSize.toByte()

        astTrees.forEach {
            it.compile(byteCodeStore)
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
            MoveOpecode.createByteCode(registerAt, offset++).forEach { byteCodeStore.addByteCode(it) }
        }

        var registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        CallOpecode.createByteCode(registerAt, astTrees.size.toByte()).forEach { byteCodeStore.addByteCode(it) }

        registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        MoveOpecode.createByteCode(byteCodeStore.stackFrameSize.toByte(), registerAt).forEach { byteCodeStore.addByteCode(it) }
    }

    /**
     * environmentは大域変数だったり、別の関数の局所変数になる
     * この関数ないで使用できる変数の情報が入っている
     *
     * @param variableEnvironment
     * @param leftValue
     * @return
     */
    override fun evaluate(variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return when (leftValue) {
            is OmuretuFunction -> {
                evaluateWhenOmuretuFunction(leftValue, variableEnvironment)
            }
            is NativeFunction -> {
                evaluateWhenNativeFunction(leftValue, variableEnvironment)
            }
            else -> {
                throw OmuretuException("bad function type", this)
            }
        }
    }

    private fun evaluateWhenOmuretuFunction(function: OmuretuFunction, variableEnvironment: VariableEnvironment): Any {
        val globalEnvironment = variableEnvironment as? GlobalVariableEnvironment
                ?: throw OmuretuException("functioni only call in global scope", this)
        if (astTrees.size != function.parameters.parameterNames.size) throw OmuretuException("bad number of argument", this)
        // パラメータの値をenvironmentに追加
        function.parameters.parameterEnvironmentKeys?.forEachIndexed { index, environmentKey ->
            val value = astTrees[index].evaluate(globalEnvironment)
            val offset = environmentKey.index
            variableEnvironment.omuretuVirtualMachine.stack[offset] = value
        } ?: throw OmuretuException("cannnot find parameter location", this)

        globalEnvironment.omuretuVirtualMachine.heapMemory = function.variableEnvironment as HeapMemory

        val byteCodeStore = globalEnvironment.byteCodeStore
        globalEnvironment.omuretuVirtualMachine.run(byteCodeStore, function.entry)

        globalEnvironment.omuretuVirtualMachine.heapMemory = variableEnvironment

        return variableEnvironment.omuretuVirtualMachine.stack[0] ?: -1
    }

    private fun evaluateWhenNativeFunction(function: NativeFunction, variableEnvironment: VariableEnvironment): Any {
        if (astTrees.size != function.numberOfParameter) throw OmuretuException("bad number of argument", this)
        val parameters = astTrees.map { it.evaluate(variableEnvironment) }.toTypedArray()
        return try {
            function.method.invoke(null, *parameters)
        } catch (exception: Exception) {
            throw OmuretuException("bad native function call: ${function.name}")
        }
    }
}