package omuretu.ast.statement

import omuretu.ast.listeral.NameLiteral
import parser.ast.ASTList
import parser.ast.ASTTree

class ParameterStmnt(
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
                ParameterStmnt(names)
            } else {
                null
            }
        }
    }

    val parameterNames: List<String>
        get() = nameLiterals.map { it.name }
}