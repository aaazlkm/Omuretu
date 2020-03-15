package ast

import parser.ASTTreeFactory
import parser.ast.ASTList
import parser.ast.ASTTree


class PrimaryExpression(
        astTrees: List<ASTTree>
) : ASTList(astTrees) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return if (argument.size == 1) argument[0] else PrimaryExpression(argument)
        }
    }
}
