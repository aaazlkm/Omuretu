package omuretu.ast

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import parser.Environment
import parser.ast.ASTList
import parser.ast.ASTTree

class BlockStmnt(
        val astTrees: List<ASTTree>
) : ASTList(astTrees) {
    companion object Factory : FactoryMethod  {
        val BLOCK_START = "{"
        val BLOCK_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return BlockStmnt(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        var result: Any? = null
        astTrees.forEach {
            result = it.evaluate(environment)
        }
        return result ?: OMURETU_DEFAULT_RETURN_VALUE // FIXME うまくない
    }
}
