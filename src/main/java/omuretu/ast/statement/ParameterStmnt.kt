package omuretu.ast.statement

import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.ast.listeral.IdNameLiteral
import parser.ast.ASTList
import parser.ast.ASTTree

class ParameterStmnt(
        private val idNameLiterals: List<IdNameLiteral>
) : ASTList(idNameLiterals) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARAMETER_BREAK = ","
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val names = argument.mapNotNull { it as? IdNameLiteral }
            return if (names.size == argument.size) {
                ParameterStmnt(names)
            } else {
                null
            }
        }
    }

    val parameterNames: List<String>
        get() = idNameLiterals.map { it.name }

    var parameterEnvironmentKeys: Array<EnvironmentKey>? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val parameterLocation = arrayOfNulls<EnvironmentKey>(idNameLiterals.size)
        idNameLiterals.forEachIndexed { index, idNameLiteral ->
            val location = idNameLocationMap.putAndReturnLocation(idNameLiteral.name)
            parameterLocation[index] = EnvironmentKey(location.ancestorAt, location.indexInIdNames)
        }
        this.parameterEnvironmentKeys = parameterLocation.mapNotNull { it }.toTypedArray()
    }
}