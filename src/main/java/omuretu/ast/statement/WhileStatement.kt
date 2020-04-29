package omuretu.ast.statement

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.BConstOpecode
import omuretu.vertualmachine.opecode.GotoOpecode
import omuretu.vertualmachine.opecode.IfZeroOpecode
import parser.ast.ASTList
import parser.ast.ASTTree
import util.ex.sliceByByte

class WhileStatement(
        val condition: ASTTree,
        val body: BlockStatement
) : ASTList(listOf(condition, body)) {
    companion object Factory: FactoryMethod {
        const val KEYWORD_WHILE = "while"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val body = argument[1] as? BlockStatement ?: return null
            return WhileStatement(argument[0], body)
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val conditionType = condition.checkType(typeEnvironment)
        val bodyType = body.checkType(typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int, this, typeEnvironment)
        return TypeCheckHelper.union(bodyType, Type.Defined.Int, typeEnvironment) // whileのbodyが一度も実行されない場合Intを返すためunion(Type.Int)している
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        val registerPosition = byteCodeStore.registerPosition

        var registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        BConstOpecode.createByteCode(0, registerAt)

        val codePositionStartCondition = byteCodeStore.codePosition
        condition.compile(byteCodeStore)

        val codePositionStartIfZero = byteCodeStore.codePosition
        registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        IfZeroOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでbody文が終わる位置を渡す

        byteCodeStore.registerPosition = registerPosition
        body.compile(byteCodeStore)

        GotoOpecode.createByteCode((codePositionStartCondition - byteCodeStore.codePosition).toShort()).forEach { byteCodeStore.addByteCode(it) }
        (byteCodeStore.codePosition - codePositionStartIfZero).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode((codePositionStartIfZero + IfZeroOpecode.SHORT_START) + index, byte)
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        var bodyResult: Any = OMURETU_DEFAULT_RETURN_VALUE
        while (true) {
            val conditionResult = condition.evaluate(variableEnvironment)
            if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
                bodyResult = body.evaluate(variableEnvironment)
            } else {
                return bodyResult
            }
        }
    }

    override fun toString(): String {
        return "($KEYWORD_WHILE $condition $body)"
    }
}
