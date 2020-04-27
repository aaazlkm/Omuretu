package omuretu.ast.binaryexpression.operator

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.PrimaryExpression
import omuretu.ast.binaryexpression.operator.base.LeftValueOperator
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.exception.OmuretuException
import omuretu.model.InlineCache
import omuretu.model.Object
import parser.ast.ASTTree

class AssignmentOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val variableEnvironment: VariableEnvironment
) : LeftValueOperator {

    override fun calculate(inlineCache: InlineCache?, doOnSaveInlineCache: ((InlineCache) -> Unit)?): Any {
        return when (leftTree) {
            is PrimaryExpression -> calculateWhenPrimaryExpression(leftTree, inlineCache, doOnSaveInlineCache)
            is IdNameLiteral -> calculateWhenNameLiteral(leftTree)
            else -> throw OmuretuException("failed to operator: $this")
        }
    }

    // TODO この処理を`primaryExpression`に閉じ込めてもいいかも
    private fun calculateWhenPrimaryExpression(
            primaryExpression: PrimaryExpression,
            inlineCache: InlineCache?,
            doOnSaveInlineCache: ((InlineCache) -> Unit)?
    ): Any {
        val firstPostFix = primaryExpression.firstPostFix
        when (firstPostFix) {
            is DotPostfix -> {
                val objectt = primaryExpression.obtainObject(variableEnvironment) as? Object ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.evaluate(variableEnvironment)
                if (objectt.classs == inlineCache?.classs) {
                    objectt.putMember(inlineCache.location, rightValue)
                } else {
                    val location = objectt.getMemberLocationOf(firstPostFix.name) ?: throw OmuretuException("undifined name: ${firstPostFix.name}")
                    doOnSaveInlineCache?.invoke(InlineCache(objectt.classs, location))
                    objectt.putMember(location, rightValue)
                }
                return rightValue
            }
            is ArrayPostfix -> {
                val index = firstPostFix.index.evaluate(variableEnvironment) as? Int ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.evaluate(variableEnvironment)
                (primaryExpression.obtainObject(variableEnvironment) as? MutableList<Any>)?.set(index, rightValue)
                return rightValue
            }
            else -> {
                throw OmuretuException("failed to operator: $this")
            }
        }
    }

    private fun calculateWhenNameLiteral(idNameLiteral: IdNameLiteral): Any {
        val rightValue = rightTree.evaluate(variableEnvironment)
        idNameLiteral.evaluateForAssign(variableEnvironment, rightValue)
        return rightValue
    }
}