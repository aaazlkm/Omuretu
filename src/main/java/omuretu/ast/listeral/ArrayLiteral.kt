package omuretu.ast.listeral

import omuretu.Environment
import parser.ast.ASTList
import parser.ast.ASTTree

class ArrayLiteral(
        private val elements: List<ASTTree>
) : ASTList(elements) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACKETS_START = "["
        const val KEYWORD_BRACKETS_END = "]"
        const val KEYWORD_PARAMETER_BREAK = ","


        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ArrayLiteral(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        val results = mutableListOf<Any>()
        elements.forEach {
            results.add(it.evaluate(environment))
        }
        return results
    }
}