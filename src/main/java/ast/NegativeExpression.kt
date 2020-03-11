package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class NegativeExpression(
        val operand: ASTTree
) : ASTList(listOf(operand)) {

    override fun toString(): String {
        return ""
//        return "-" + operand()
    }
}