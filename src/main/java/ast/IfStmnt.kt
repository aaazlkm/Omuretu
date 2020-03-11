package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class IfStmnt(
        val condition: ASTTree,
        val thenBlock: ASTTree,
        val elseBlock: ASTTree
) : ASTList(listOf(condition, thenBlock, elseBlock)) {

    override fun toString(): String {
        return ("(if $condition $thenBlock else $elseBlock)")
    }
}