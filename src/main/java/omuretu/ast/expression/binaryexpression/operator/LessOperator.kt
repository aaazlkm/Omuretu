package omuretu.ast.expression.binaryexpression.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class LessOperator(
    override val leftTree: ASTTree,
    override val rightTree: ASTTree
) : RightValueOperator {
    override fun checkType(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        val leftType = leftTree.accept(checkTypeVisitor, typeEnvironment)
        val rightType = rightTree.accept(checkTypeVisitor, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int(), leftType, leftTree, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int(), rightType, leftTree, typeEnvironment)
        return Type.Defined.Int()
    }

    override fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        val leftValue = leftTree.accept(evaluateVisitor, variableEnvironment)
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            if (leftValue < rightValue) OMURETU_TRUE else OMURETU_FALSE
        } else {
            throw OmuretuException("failed to operator: $this")
        }
    }
}
