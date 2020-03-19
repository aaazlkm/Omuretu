package omuretu.ast.binaryexpression.operator

import omuretu.Environment
import omuretu.ast.listeral.NameLiteral
import omuretu.ast.PrimaryExpression
import omuretu.ast.binaryexpression.operator.base.Operator
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.exception.OmuretuException
import omuretu.model.Object
import parser.ast.ASTTree

class AssignmentOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val environment: Environment
) : Operator {
    override fun calculate(): Any {
        return when (leftTree) {
            is PrimaryExpression -> calculateWhenPrimaryExpression(leftTree, rightTree, environment)
            is NameLiteral -> calculateWhenNameLiteral(leftTree, rightTree, environment)
            else -> throw OmuretuException("failed to operator: $this")
        }
    }

    // TODO この処理を`primaryExpression`に閉じ込めてもいいかも
    private fun calculateWhenPrimaryExpression(primaryExpression: PrimaryExpression, rightTree: ASTTree, environment: Environment): Any {
        val firstPostFix = primaryExpression.firstPostFix
        when(firstPostFix) {
            is DotPostfix -> {
                val objectt = primaryExpression.obtainObject(environment) as? Object ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.evaluate(environment)
                objectt.putMember(firstPostFix.name, rightValue)
                return rightValue
            }
            is ArrayPostfix -> {
                val index = firstPostFix.index.evaluate(environment) as? Int ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.evaluate(environment)
                (primaryExpression.obtainObject(environment) as? MutableList<Any>)?.set(index, rightValue)
                return rightValue
            }
            else -> {
                throw OmuretuException("failed to operator: $this")
            }
        }
    }

    private fun calculateWhenNameLiteral(nameLiteral: NameLiteral, rightTree: ASTTree, environment: Environment): Any {
        val rightValue = rightTree.evaluate(environment)
        environment.put(nameLiteral.name, rightValue)
        return rightValue
    }
}