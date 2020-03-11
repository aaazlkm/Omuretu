package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class WhileStmnt(
        val condition: ASTTree,
        val body: ASTTree
) : ASTList(listOf(condition, body)) {

    override fun toString(): String {
        return "(while $condition $body)"
    }
}
