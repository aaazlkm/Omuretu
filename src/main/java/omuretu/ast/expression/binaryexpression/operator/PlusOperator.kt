package omuretu.ast.expression.binaryexpression.operator

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import parser.ast.ASTTree

class PlusOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val variableEnvironment: VariableEnvironment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(variableEnvironment)
        val rightValue = rightTree.evaluate(variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue + rightValue
        } else {
            "$leftValue$rightValue"
        }
    }
}
