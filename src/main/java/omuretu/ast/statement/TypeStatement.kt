package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
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

class TypeStatement(
    private val typeName: IdNameLiteral? = null
) : ASTList(typeName?.let { listOf(it) } ?: listOf()) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_COLON = ":"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 0..1) return null
            return when (argument.size) {
                0 -> TypeStatement()
                1 -> {
                    val typeName = argument[0] as? IdNameLiteral ?: return null
                    return TypeStatement(typeName)
                }
                else -> null
            }
        }
    }

    val name: String
        get() = typeName?.name ?: Type.NeedInference.NAME

    var environmentKey: EnvironmentKey? = null

    override fun toString() = "$KEYWORD_COLON $typeName"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any = OMURETU_DEFAULT_RETURN_VALUE
}
