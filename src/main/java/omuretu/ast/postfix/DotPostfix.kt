package omuretu.ast.postfix

import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.model.InlineCache
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

class DotPostfix(
        private val idNameLiteral: IdNameLiteral
) : Postfix(listOf(idNameLiteral)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_DOT = "."
        const val KEYWORD_NEW = "new"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val name = argument.getOrNull(0) as? IdNameLiteral ?: return null
            return DotPostfix(name)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var objectInlineCache: InlineCache? = null

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        return checkTypeVisitor.visit(this, typeEnvironment, leftType)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return evaluateVisitor.visit(this, variableEnvironment, leftValue)
    }
}