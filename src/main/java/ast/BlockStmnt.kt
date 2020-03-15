package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class BlockStmnt(astTrees: List<ASTTree>) : ASTList(astTrees) {
    companion object {
        val BLOCK_START = "{"
        val BLOCK_END = "}"
    }
}
