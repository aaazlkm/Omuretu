package ast

import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree

class BinaryExpression(
        val left: ASTTree,
        val operation: ASTLeaf,
        val right: ASTTree
) : ASTList(listOf(left, operation, right)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val operation = argument[1] as? ASTLeaf ?: return null
            return BinaryExpression(argument[0], operation, argument[2])
        }
    }
}
