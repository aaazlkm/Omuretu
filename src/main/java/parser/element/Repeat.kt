package parser.element

import lexer.Lexer
import parser.Parser
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree

// TODO あとで下記を消す
// check ok

/**
 * { pat } パターンpatの0回以上の繰り返しと一致
 *
 * @property parser
 */
class Repeat(var parser: Parser) : Element {
    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        while (parser.judgeNextSuccessOrNot(lexer)) {
            when (val astTree = parser.parseTokens(lexer)) {
                is ASTLeaf -> results.add(astTree)
                is ASTList -> if (astTree.numberOfChildren > 0) results.add(astTree)
            }
        }
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return parser.judgeNextSuccessOrNot(lexer)
    }
}