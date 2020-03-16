package omuretu.ast

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.Environment
import parser.ast.ASTList
import parser.ast.ASTTree

// astTreesを一つも持たない
class NullStmnt(astTrees: List<ASTTree>) : ASTList(astTrees) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return NullStmnt(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        return OMURETU_DEFAULT_RETURN_VALUE
    }
}
