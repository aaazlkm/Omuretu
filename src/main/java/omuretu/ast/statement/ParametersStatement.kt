package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
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

data class ParametersStatement(
    val parameters: List<ParameterStatement>
) : ASTList(parameters) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARAMETER_BREAK = ","
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val names = argument.mapNotNull { it as? ParameterStatement }
            return if (names.size == argument.size) {
                ParametersStatement(names)
            } else {
                null
            }
        }
    }

    val parameterNames: List<String>
        get() = parameters.map { it.name }

    val types: List<Type.Defined>
        get() = parameters.map { it.type }

    var parameterEnvironmentKeys: Array<EnvironmentKey>? = null

    override fun toString() = "$KEYWORD_PARENTHESIS_START $parameters $KEYWORD_PARENTHESIS_END"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any = OMURETU_DEFAULT_RETURN_VALUE
}
