package ast

import parser.ast.ASTTree
import parser.ast.AstListWithChildren


class PrimaryExpression(astTrees: List<ASTTree>) : AstListWithChildren(astTrees) {
    override fun make(astTrees: List<ASTTree>): ASTTree {
        return if (children.size == 1) children[0] else PrimaryExpression(children)
    }
}
