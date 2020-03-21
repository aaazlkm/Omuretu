package omuretu.ast.binaryexpression.operator

import omuretu.environment.Environment
import omuretu.ast.binaryexpression.operator.base.Operator
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class SurplusOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
) : Operator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(environment)
        val rightValue = rightTree.evaluate(environment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue % rightValue
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}