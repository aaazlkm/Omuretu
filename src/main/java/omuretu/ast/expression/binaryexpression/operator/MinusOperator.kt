package omuretu.ast.expression.binaryexpression.operator

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.exception.OmuretuException
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class MinusOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val evaluateVisitor: EvaluateVisitor,
        override val variableEnvironment: VariableEnvironment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.accept(evaluateVisitor, variableEnvironment)
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue - rightValue
        } else if (leftValue is Long && rightValue is Long) {
            leftValue - rightValue
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}