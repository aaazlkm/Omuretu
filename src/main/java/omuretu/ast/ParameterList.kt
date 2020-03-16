package omuretu.ast

import parser.ast.ASTList
import parser.ast.ASTTree

class ParameterList(
        private val nameLiterals: List<NameLiteral>
) : ASTList(nameLiterals) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARAMETER_BREAK = ","
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val names = argument.mapNotNull { it as? NameLiteral }
            return if (names.size == argument.size) {
                ParameterList(names)
            } else {
                null
            }
        }
    }

    val parameterNames: List<String>
        get() = nameLiterals.map { it.name }
}