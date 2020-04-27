package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.environment.base.VariableEnvironment
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

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        return OMURETU_DEFAULT_RETURN_VALUE
    }
}
