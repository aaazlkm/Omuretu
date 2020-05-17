package omuretu.ast.expression.binaryexpression.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class EqualOperator(
    override val leftTree: ASTTree,
    override val rightTree: ASTTree
) : RightValueOperator {
    override fun checkType(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return Type.Defined.Int()
    }

    override fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        val leftValue = leftTree.accept(evaluateVisitor, variableEnvironment)
        val rightValue = rightTree.accept(evaluateVisitor, variableEnvironment)
        return if (leftValue == rightValue) OMURETU_TRUE else OMURETU_FALSE
    }
}
