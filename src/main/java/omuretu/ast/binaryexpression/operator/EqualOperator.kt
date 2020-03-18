package omuretu.ast.binaryexpression.operator

import omuretu.Environment
import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.Operator
import parser.ast.ASTTree

class EqualOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
) : Operator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(environment)
        val rightValue = rightTree.evaluate(environment)
        return if (leftValue == rightValue) OMURETU_TRUE else OMURETU_FALSE
    }
}