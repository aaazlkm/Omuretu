package ast

import parser.ASTTreeFactory
import parser.ast.ASTList
import parser.ast.ASTTree

class IfStmnt(
        val condition: ASTTree,
        val thenBlock: ASTTree,
        val elseBlock: ASTTree? = null
) : ASTList(elseBlock?.let { listOf(condition, thenBlock, it) } ?: listOf(condition, thenBlock)) {
    companion object Factory: FactoryMethod {
        const val KEYWORD_IF = "if"
        const val KEYWORD_ELSE = "else"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 2..3) return null
            return when(argument.size) {
                2 -> IfStmnt(argument[0], argument[1])
                3 -> IfStmnt(argument[0], argument[1], argument[2])
                else -> null
            }
        }
    }

    override fun toString(): String {
        return ("($KEYWORD_IF $condition $thenBlock $KEYWORD_ELSE $elseBlock)")
    }
}