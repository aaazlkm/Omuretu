package omuretu.ast.binaryexpression.operator

import omuretu.environment.base.VariableEnvironment
import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.RightValueOperator
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class LessOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val variableEnvironment: VariableEnvironment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(variableEnvironment)
        val rightValue = rightTree.evaluate(variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            if (leftValue < rightValue) OMURETU_TRUE else OMURETU_FALSE
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}