package ast

import parser.ast.ASTList
import parser.ast.ASTTree

class BinaryExpression(
        private val left: ASTTree,
        private val operation: Operation,
        private val right: ASTTree
) : ASTList(listOf(left, operation, right))
