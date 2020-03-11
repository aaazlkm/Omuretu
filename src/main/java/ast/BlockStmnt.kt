package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class BlockStmnt(astTrees: List<ASTTree>) : ASTList(astTrees)
