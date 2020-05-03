package parser.element

import lexer.Lexer
import parser.Parser
import parser.ast.ASTList
import parser.ast.ASTTree

/**
 * [ pat ] パターンpatと0回または1回だけ一致
 *
 * @property parser
 */
class Option(var parser: Parser) : Element {
    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        if (parser.judgeNextSuccessOrNot(lexer)) {
            val astTree = parser.parseTokens(lexer)
            // 余計な枝を作らないように
            if (astTree::class.java != ASTList::class.java || (astTree as ASTList).numberOfChildren > 0) {
                results.add(astTree)
            }
        }
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return parser.judgeNextSuccessOrNot(lexer)
    }
}
