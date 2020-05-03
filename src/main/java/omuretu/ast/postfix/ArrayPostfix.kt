package omuretu.ast.postfix

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class ArrayPostfix(
    val index: ASTTree
) : Postfix(listOf(index)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACKETS_START = "["
        const val KEYWORD_BRACKETS_END = "]"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 1) return null
            return ArrayPostfix(argument.first())
        }
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        return checkTypeVisitor.visit(this, typeEnvironment, leftType)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return evaluateVisitor.visit(this, variableEnvironment, leftValue)
    }
}
