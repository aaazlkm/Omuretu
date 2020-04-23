package omuretu.ast.postfix

import omuretu.environment.Environment
import omuretu.environment.GlobalEnvironment
import omuretu.environment.NestedEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function.OmuretuFunction
import omuretu.model.Function.NativeFunction
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

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    /**
     * environmentは大域変数だったり、別の関数の局所変数になる
     * この関数ないで使用できる変数の情報が入っている
     *
     * @param environment
     * @param leftValue
     * @return
     */
    override fun evaluate(environment: Environment, leftValue: Any): Any {
        return when (leftValue) {
            is OmuretuFunction -> {
                evaluateWhenOmuretuFunction(leftValue, environment)
            }
            is NativeFunction -> {
                evaluateWhenNativeFunction(leftValue, environment)
            }
            else -> {
                throw OmuretuException("bad function type", this)
            }
        }
    }

    private fun evaluateWhenOmuretuFunction(function: OmuretuFunction, environment: Environment): Any {
        val globalEnvironment = environment as? GlobalEnvironment ?: throw OmuretuException("functioni only call in global scope", this)
        if (astTrees.size != function.parameters.parameterNames.size) throw OmuretuException("bad number of argument", this)
        // パラメータの値をenvironmentに追加
        function.parameters.parameterEnvironmentKeys?.forEachIndexed { index, environmentKey ->
            val value = astTrees[index].evaluate(globalEnvironment)
            val offset = environmentKey.index
            environment.omuretuVirtualMachine.stack[offset] = value
        } ?: throw OmuretuException("cannnot find parameter location", this)

        globalEnvironment.omuretuVirtualMachine.heapMemory = function.environment as HeapMemory

        val byteCodeStore = globalEnvironment.byteCodeStore
        globalEnvironment.omuretuVirtualMachine.run(byteCodeStore, function.entry)

        return environment.omuretuVirtualMachine.stack[0] ?: -1
    }

    private fun evaluateWhenNativeFunction(function: NativeFunction, environment: Environment): Any {
        if (astTrees.size != function.numberOfParameter) throw OmuretuException("bad number of argument", this)
        val parameters = astTrees.map { it.evaluate(environment) }.toTypedArray()
        return try {
            function.method.invoke(null, *parameters)
        } catch (exception: Exception) {
            throw OmuretuException("bad native function call: ${function.name}")
        }
    }
}