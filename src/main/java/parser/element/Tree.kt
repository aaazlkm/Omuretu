package parser.element

import lexer.Lexer
import parser.Parser
import parser.ast.ASTTree

// TODO あとで下記を消す
// check ok
class Tree(private val parser: Parser) : Element {
    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        results.add(parser.parseTokens(lexer))
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return parser.judgeNextSuccessOrNot(lexer)
    }
}