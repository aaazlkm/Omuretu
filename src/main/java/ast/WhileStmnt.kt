package ast

import parser.ast.ASTList
import parser.ast.ASTTree

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

    override fun toString(): String {
        return "($KEYWORD_WHILE $condition $body)"
    }
}
