package omuretu.ast.binaryexpression.operator

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.binaryexpression.operator.base.RightValueOperator
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class MinusOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val variableEnvironment: VariableEnvironment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(variableEnvironment)
        val rightValue = rightTree.evaluate(variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue - rightValue
        } else if (leftValue is Long && rightValue is Long) {
            leftValue - rightValue
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}