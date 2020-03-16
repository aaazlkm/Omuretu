package omuretu.ast

import omuretu.exception.OmuretuException
import omuretu.Environment
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

    override fun evaluate(environment: Environment): Any {
        val result = operand.evaluate(environment)
        return if (result is Int) {
            -result
        } else {
            throw OmuretuException("bad type for  -", this)
        }
    }

    override fun toString(): String {
        return "-$operand"
    }
}