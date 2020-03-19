package omuretu.ast.postfix

import omuretu.Environment
import omuretu.exception.OmuretuException
import parser.ast.ASTTree

class ArrayPostfix(
        val index: ASTTree
) : Postfix(listOf(index)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACKETS_START = "["
        const val KEYWORD_BRACKETS_END = "]"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 1) return null
            return ArrayPostfix(argument.first())
        }
    }

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    override fun evaluate(environment: Environment, leftValue: Any): Any {
        val list = (leftValue as? MutableList<*>)?.mapNotNull { it } ?: throw OmuretuException("bad array access")
        val index = index.evaluate(environment) as? Int ?: throw OmuretuException("bad array access")
        return list[index]
    }
}