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

class ClassStatement(
    private val idNameLiteral: IdNameLiteral,
    val bodyStatement: ClassBodyStatement
) : ASTList(listOf(idNameLiteral, bodyStatement)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLASS = "class"
        const val KEYWORD_THIS = "this"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            when (argument.size) {
                2 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[1] as? ClassBodyStatement ?: return null
                    return ClassStatement(nameLiteral, bodyStmnt)
                }
                else -> {
                    return null
                }
            }
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    var typeEnvironment: TypeEnvironment? = null

    override fun toString() = "$KEYWORD_CLASS $idNameLiteral $bodyStatement"

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
