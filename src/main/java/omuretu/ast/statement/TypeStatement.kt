package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
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
            return when(argument.size) {
                0 -> TypeStatement()
                1 -> {
                    val typeName = argument[0] as? IdNameLiteral ?: return null
                    return TypeStatement(typeName)
                }
                else -> null
            }
        }
    }

    private val name: String
        get() = typeName?.name ?: Type.NeedInference.NAME

    val type: Type?
        get() = Type.from(name)

    override fun toString() = "$KEYWORD_COLON $typeName"

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type = type ?: Type.Defined.Any

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any = OMURETU_DEFAULT_RETURN_VALUE
}