package omuretu.ast.expression.binaryexpression.operator

import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class PlusOperator(
    override val leftTree: ASTTree,
    override val rightTree: ASTTree
) : RightValueOperator {
    override fun checkType(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        val leftType = leftTree.accept(checkTypeVisitor, typeEnvironment)
        val rightType = rightTree.accept(checkTypeVisitor, typeEnvironment)
        return TypeCheckHelper.plus(leftType, rightType, typeEnvironment)
    }

    override fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        val leftValue = leftTree.accept(evaluateVisitor, variableEnvironment)
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        return if (leftValue is Int && rightValue is Int) {
            leftValue + rightValue
        } else {
            "$leftValue$rightValue"
        }
    }
}
