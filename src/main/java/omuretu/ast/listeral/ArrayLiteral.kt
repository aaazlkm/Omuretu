package omuretu.ast.listeral

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

class ArrayLiteral(
    val elements: List<ASTTree>
) : ASTList(elements) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACKETS_START = "["
        const val KEYWORD_BRACKETS_END = "]"
        const val KEYWORD_PARAMETER_BREAK = ","

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ArrayLiteral(argument)
        }
    }

    override fun toString() = "$KEYWORD_BRACKETS_START ${elements.fold("") { acc, s -> "$acc$s" }} $KEYWORD_BRACKETS_END"

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
