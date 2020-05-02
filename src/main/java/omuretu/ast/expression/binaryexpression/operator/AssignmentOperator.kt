package omuretu.ast.expression.binaryexpression.operator

import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.LeftValueOperator
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.InlineCache
import omuretu.model.Object
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class AssignmentOperator(
        override val leftTree: ASTTree,
        override val rightTree: ASTTree,
        override val evaluateVisitor: EvaluateVisitor,
        override val variableEnvironment: VariableEnvironment
) : LeftValueOperator {

    override fun calculate(inlineCache: InlineCache?, doOnSaveInlineCache: ((InlineCache) -> Unit)?): Any {
        return when (leftTree) {
            is PrimaryExpression -> calculateWhenPrimaryExpression(leftTree, inlineCache, doOnSaveInlineCache)
            is IdNameLiteral -> calculateWhenNameLiteral(leftTree)
            else -> throw OmuretuException("failed to operator: $this")
        }
    }

    private fun calculateWhenPrimaryExpression(
            primaryExpression: PrimaryExpression,
            inlineCache: InlineCache?,
            doOnSaveInlineCache: ((InlineCache) -> Unit)?
    ): Any {
        when (val firstPostFix = primaryExpression.firstPostFix) {
            is DotPostfix -> {
                val objectt = primaryExpression.obtainObject(evaluateVisitor, variableEnvironment) as? Object ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
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
                val index = firstPostFix.index.accept(evaluateVisitor, variableEnvironment) as? Int
                        ?: throw OmuretuException("failed to operator: $this")
                val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
                (primaryExpression.obtainObject(evaluateVisitor, variableEnvironment) as? MutableList<Any>)?.set(index, rightValue)
                return rightValue
            }
            else -> {
                throw OmuretuException("failed to operator: $this")
            }
        }
    }

    private fun calculateWhenNameLiteral(idNameLiteral: IdNameLiteral): Any {
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        idNameLiteral.environmentKey?.let {
            variableEnvironment.put(it, rightValue)
        } ?: throw OmuretuException("undefined name:", idNameLiteral)
        return rightValue
    }
}