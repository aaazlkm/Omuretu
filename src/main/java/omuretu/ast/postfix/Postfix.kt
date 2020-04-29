package omuretu.ast.postfix

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import parser.ast.ASTList
import parser.ast.ASTTree

abstract class Postfix(children: List<ASTTree>): ASTList(children) {
    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        throw OmuretuException("must be called `checkType(typeEnvironment: TypeEnvironment): Type` instead of this method", this)
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    abstract fun evaluate(variableEnvironment: VariableEnvironment, leftValue: Any): Any

    abstract fun checkType(typeEnvironment: TypeEnvironment, leftType: Type): Type
}