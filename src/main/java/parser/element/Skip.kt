package parser.element

import lexer.token.Token
import parser.ast.ASTTree

// TODO あとで下記を消す
// check ok
class Skip constructor(vararg tokens: String) : Leaf(*tokens) {
     // 何もしない
     override fun addResult(results: MutableList<ASTTree>, token: Token) {}
}