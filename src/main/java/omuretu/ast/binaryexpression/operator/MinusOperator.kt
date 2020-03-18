package omuretu.ast.binaryexpression.operator

import omuretu.Environment
import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.Operator
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class MinusOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
) : Operator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(environment)
        val rightValue = rightTree.evaluate(environment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue - rightValue
        } else if (leftValue is Long && rightValue is Long) {
            leftValue - rightValue
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}