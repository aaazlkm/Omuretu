package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class NegativeExpression(
        val operand: ASTTree
) : ASTList(listOf(operand)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTList? {
            if (argument.size != 1) return null
            return NegativeExpression(argument[0])
        }
    }

    override fun toString(): String {
        return "-$operand"
    }
}