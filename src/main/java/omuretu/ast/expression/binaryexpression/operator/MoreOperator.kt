package omuretu.ast.expression.binaryexpression.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class MoreOperator(
    override val leftTree: ASTTree,
    override val rightTree: ASTTree,
    override val evaluateVisitor: EvaluateVisitor,
    override val variableEnvironment: VariableEnvironment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.accept(evaluateVisitor, variableEnvironment)
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            if (leftValue > rightValue) OMURETU_TRUE else OMURETU_FALSE
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}
