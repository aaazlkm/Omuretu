package omuretu.ast.binaryexpression.operator

import omuretu.environment.Environment
import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.Operator
import omuretu.ast.binaryexpression.operator.base.RightValueOperator
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class GreaterOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
): RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(environment)
        val rightValue = rightTree.evaluate(environment)
        return if (leftValue is Int && rightValue is Int) {
            if (leftValue > rightValue) OMURETU_TRUE else OMURETU_FALSE
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}