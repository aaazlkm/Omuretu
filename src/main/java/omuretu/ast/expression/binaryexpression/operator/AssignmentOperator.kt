package omuretu.ast.expression.binaryexpression.operator

import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.LeftValueOperator
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.exception.TypeException
import omuretu.model.InlineCache
import omuretu.model.Object
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class AssignmentOperator(
    override val leftTree: ASTTree,
    override val rightTree: ASTTree
) : LeftValueOperator {
    //region check type methods

    override fun checkType(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return when (leftTree) {
            is PrimaryExpression -> checkTypeWhenPrimaryExpression(leftTree, checkTypeVisitor, typeEnvironment)
            is IdNameLiteral -> checkTypeWhenIdNameLiteral(leftTree, checkTypeVisitor, typeEnvironment)
            else -> throw OmuretuException("failed to operator: $this")
        }
    }

    private fun checkTypeWhenPrimaryExpression(primaryExpression: PrimaryExpression, checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return when (val firstPostFix = primaryExpression.firstPostFix) {
            is DotPostfix -> {
                TODO("オブジェクトに関しての型を実装すること")
            }
            is ArrayPostfix -> {
                firstPostFix.index.accept(checkTypeVisitor, typeEnvironment).let {
                    if (it !is Type.Defined.Int) throw OmuretuException("failed to operator: $this")
                }
                val rightType = rightTree.accept(checkTypeVisitor, typeEnvironment)
                val arrayType = primaryExpression.literal.accept(checkTypeVisitor, typeEnvironment) as? Type.Defined.Array
                        ?: throw TypeException("need array type", primaryExpression)
                TypeCheckHelper.checkSubTypeOrThrow(arrayType.type, rightType, primaryExpression, typeEnvironment)
                rightType
            }
            else -> {
                throw OmuretuException("failed to operator: $this")
            }
        }
    }

    private fun checkTypeWhenIdNameLiteral(idNameLiteral: IdNameLiteral, checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return idNameLiteral.checkTypeForAssign(typeEnvironment, rightTree.accept(checkTypeVisitor, typeEnvironment))
    }

    //endregion

    //region calculate methods

    override fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, inlineCache: InlineCache?, doOnSaveInlineCache: ((InlineCache) -> Unit)?): Any {
        return when (leftTree) {
            is PrimaryExpression -> calculateWhenPrimaryExpression(evaluateVisitor, variableEnvironment, leftTree, inlineCache, doOnSaveInlineCache)
            is IdNameLiteral -> calculateWhenNameLiteral(evaluateVisitor, variableEnvironment, leftTree)
            else -> throw OmuretuException("failed to operator: $this")
        }
    }

    private fun calculateWhenPrimaryExpression(
        evaluateVisitor: EvaluateVisitor,
        variableEnvironment: VariableEnvironment,
        primaryExpression: PrimaryExpression,
        inlineCache: InlineCache?,
        doOnSaveInlineCache: ((InlineCache) -> Unit)?
    ): Any {
        when (val firstPostFix = primaryExpression.firstPostFix) {
            is DotPostfix -> {
                val objectt = primaryExpression.obtainObject(evaluateVisitor, variableEnvironment) as? Object
                        ?: throw OmuretuException("failed to operator: $this")
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

    private fun calculateWhenNameLiteral(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, idNameLiteral: IdNameLiteral): Any {
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        idNameLiteral.environmentKey?.let {
            variableEnvironment.put(it, rightValue)
        } ?: throw OmuretuException("undefined name:", idNameLiteral)
        return rightValue
    }

    //endregion
}
