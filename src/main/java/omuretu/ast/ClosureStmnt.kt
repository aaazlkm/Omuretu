package omuretu.ast

import omuretu.Environment
import omuretu.model.Function
import parser.ast.ASTList
import parser.ast.ASTTree

class ClosureStmnt(
        val parameters: ParameterList,
        val blockStmnt: BlockStmnt
) : ASTList(listOf(parameters, blockStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLOSURE = "closure"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val parameters = argument[0] as? ParameterList ?: return null
            val blockStmnt = argument[1] as? BlockStmnt ?: return null
            return ClosureStmnt(parameters, blockStmnt)
        }
    }

    override fun evaluate(environment: Environment): Any {
        return Function.OmuretuFunction(parameters, blockStmnt, environment)
    }

    override fun toString(): String {
        return "(closure $parameters $blockStmnt)"
    }
}