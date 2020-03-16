package omuretu.ast

import omuretu.Environment
import parser.ast.ASTList
import parser.ast.ASTTree
import omuretu.model.Function

class DefStmnt(
        val nameLiteral: NameLiteral,
        val parameters: ParameterList,
        val blockStmnt: BlockStmnt
) : ASTList(listOf(nameLiteral, parameters, blockStmnt)) {
    companion object Factory : FactoryMethod  {
        const val KEYWORD_DEF = "def"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val name = argument[0] as? NameLiteral ?: return null
            val parameters = argument[1] as? ParameterList ?: return null
            val blockStmnt = argument[2] as? BlockStmnt ?: return null
            return DefStmnt(name, parameters, blockStmnt)
        }
    }

    override fun evaluate(environment: Environment): Any {
        environment.put(nameLiteral.token.id, Function(parameters, blockStmnt, environment))
        return nameLiteral.token.id
    }

    override fun toString(): String {
        return "(def $nameLiteral $parameters $blockStmnt)"
    }
}