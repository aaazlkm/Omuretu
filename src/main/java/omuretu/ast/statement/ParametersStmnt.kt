package omuretu.ast.statement

import omuretu.environment.base.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import parser.ast.ASTList
import parser.ast.ASTTree

class ParametersStmnt(
        private val parameters: List<ParameterStmnt>
) : ASTList(parameters) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARAMETER_BREAK = ","
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val names = argument.mapNotNull { it as? ParameterStmnt }
            return if (names.size == argument.size) {
                ParametersStmnt(names)
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

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val parameterLocation = arrayOfNulls<EnvironmentKey>(parameters.size)
        parameters.forEachIndexed { index, idNameLiteral ->
            val location = idNameLocationMap.putAndReturnLocation(idNameLiteral.name)
            parameterLocation[index] = EnvironmentKey(location.ancestorAt, location.indexInIdNames)
        }
        this.parameterEnvironmentKeys = parameterLocation.mapNotNull { it }.toTypedArray()
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val parameterEnvironmentKeys = parameterEnvironmentKeys ?: throw OmuretuException("")
        if (parameters.size != parameterEnvironmentKeys.size) throw OmuretuException("")
        parameterEnvironmentKeys.zip(parameters).forEach { typeEnvironment.put(it.first, it.second.checkType(typeEnvironment)) }
        return Type.Defined.Any // パラメータの型はなんでもいい
    }
}