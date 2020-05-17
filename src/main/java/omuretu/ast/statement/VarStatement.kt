package omuretu.ast.statement

import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class VarStatement(
    val idNameLiteral: IdNameLiteral,
    val typeStatement: TypeStatement,
    val initializer: ASTTree
) : ASTList(listOf(idNameLiteral, typeStatement, initializer)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_VAR = "var"
        const val KEYWORD_EQUAL = "="

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val idNameLiteral = argument[0] as? IdNameLiteral ?: return null
            val typeTag = argument[1] as? TypeStatement ?: return null
            val initializer = argument[2] as? ASTTree ?: return null
            return VarStatement(idNameLiteral, typeTag, initializer)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    override fun toString() = "$KEYWORD_VAR $idNameLiteral $typeStatement $KEYWORD_EQUAL $initializer"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
