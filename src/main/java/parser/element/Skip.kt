package parser.element

import lexer.token.Token
import parser.ast.ASTTree

class Skip constructor(vararg tokens: String) : Leaf(*tokens) {
     // 何もしない
     override fun addResult(results: MutableList<ASTTree>, token: Token) {}
}