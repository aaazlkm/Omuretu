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

class IfStatement(
        val condition: ASTTree,
        val thenBlock: BlockStatement,
        val elseBlock: BlockStatement? = null
) : ASTList(elseBlock?.let { listOf(condition, thenBlock, it) } ?: listOf(condition, thenBlock)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_IF = "if"
        const val KEYWORD_ELSE = "else"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 2..3) return null
            val thenBlock = argument[1] as? BlockStatement ?: return null
            return when (argument.size) {
                2 -> IfStatement(argument[0], thenBlock)
                3 -> {
                    val elseBlock = argument[2] as? BlockStatement ?: return null
                    IfStatement(argument[0], thenBlock, elseBlock)
                }
                else -> null
            }
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val conditionType = condition.checkType(typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int, this, typeEnvironment)
        val thenBlockType = thenBlock.checkType(typeEnvironment)
        val elseBlockType = elseBlock?.checkType(typeEnvironment)
        return if (elseBlockType == null) {
            thenBlockType
        } else {
            TypeCheckHelper.union(thenBlockType, elseBlockType, typeEnvironment)
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

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        val conditionResult = condition.evaluate(variableEnvironment)
        return if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
            thenBlock.evaluate(variableEnvironment)
        } else {
            elseBlock?.evaluate(variableEnvironment) ?: OMURETU_DEFAULT_RETURN_VALUE
        }
    }

    override fun toString(): String {
        return ("($KEYWORD_IF $condition $thenBlock $KEYWORD_ELSE $elseBlock)")
    }
}