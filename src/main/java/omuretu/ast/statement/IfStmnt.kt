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

class IfStmnt(
        val condition: ASTTree,
        val thenBlock: ASTTree,
        val elseBlock: ASTTree? = null
) : ASTList(elseBlock?.let { listOf(condition, thenBlock, it) } ?: listOf(condition, thenBlock)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_IF = "if"
        const val KEYWORD_ELSE = "else"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 2..3) return null
            return when (argument.size) {
                2 -> IfStmnt(argument[0], argument[1])
                3 -> IfStmnt(argument[0], argument[1], argument[2])
                else -> null
            }
        }
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        condition.compile(byteCodeStore)
        // elseが始まる位置を格納しているCodePosition
        val codePositionStartIfZero = byteCodeStore.codePosition
        val codePositionSavingStartElseBlock = codePositionStartIfZero + IfZeroOpecode.SHORT_START
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        IfZeroOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでelse文が始まる位置を渡す

        val registerPosition = byteCodeStore.registerPosition
        thenBlock.compile(byteCodeStore)

        val codePositionStartGoto = byteCodeStore.codePosition
        val codePositionSavingEndElseBlock = byteCodeStore.codePosition + GotoOpecode.SHORT_START
        GotoOpecode.createByteCode(0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでelse文が終わる位置を渡す

        (byteCodeStore.codePosition - codePositionStartIfZero).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode(codePositionSavingStartElseBlock + index, byte)
        }

        byteCodeStore.registerPosition = registerPosition
        elseBlock?.compile(byteCodeStore) ?: run {
            // TODO 何をしているのか調査
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
            BConstOpecode.createByteCode(0, registerAt).forEach { byteCodeStore.addByteCode(it) }
        }

        (byteCodeStore.codePosition - codePositionStartGoto).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode(codePositionSavingEndElseBlock + index, byte)
        }
    }

    override fun evaluate(environment: Environment): Any {
        val conditionResult = condition.evaluate(environment)
        return if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
            thenBlock.evaluate(environment)
        } else {
            elseBlock?.evaluate(environment) ?: OMURETU_DEFAULT_RETURN_VALUE
        }
    }

    override fun toString(): String {
        return ("($KEYWORD_IF $condition $thenBlock $KEYWORD_ELSE $elseBlock)")
    }
}