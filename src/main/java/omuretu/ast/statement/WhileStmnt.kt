package omuretu.ast.statement

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.environment.Environment
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.BConstOpecode
import omuretu.vertualmachine.opecode.GotoOpecode
import omuretu.vertualmachine.opecode.IfZeroOpecode
import parser.ast.ASTList
import parser.ast.ASTTree
import util.ex.sliceByByte

class WhileStmnt(
        val condition: ASTTree,
        val body: ASTTree
) : ASTList(listOf(condition, body)) {
    companion object Factory: FactoryMethod {
        const val KEYWORD_WHILE = "while"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            return WhileStmnt(argument[0], argument[1])
        }
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

    override fun evaluate(environment: Environment): Any {
        var bodyResult: Any = OMURETU_DEFAULT_RETURN_VALUE
        while (true) {
            val conditionResult = condition.evaluate(environment)
            if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
                bodyResult = body.evaluate(environment)
            } else {
                return bodyResult
            }
        }
    }

    override fun toString(): String {
        return "($KEYWORD_WHILE $condition $body)"
    }
}
