package omuretu.ast.postfix

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

abstract class Postfix(children: List<ASTTree>): ASTList(children) {
    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        throw OmuretuException("must be called `checkType(typeEnvironment: TypeEnvironment): Type` instead of this method", this)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    abstract fun accept(checkTypeVisitor: CheckTypeVisitor,typeEnvironment: TypeEnvironment, leftType: Type): Type

    abstract fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, leftValue: Any): Any
}