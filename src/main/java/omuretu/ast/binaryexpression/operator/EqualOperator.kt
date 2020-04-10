package omuretu.ast.binaryexpression.operator

import omuretu.environment.Environment
import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.Operator
import omuretu.ast.binaryexpression.operator.base.RightValueOperator
import parser.ast.ASTTree

class EqualOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
) : RightValueOperator {
    override fun calculate(): Any {
        val leftValue = leftTree.evaluate(environment)
        val rightValue = rightTree.evaluate(environment)
        return if (leftValue == rightValue) OMURETU_TRUE else OMURETU_FALSE
    }
}