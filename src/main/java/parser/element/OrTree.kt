package parser.element

import parser.exception.ParseException
import lexer.Lexer
import parser.Parser
import parser.ast.ASTTree

// TODO あとで下記を消す
// check ok

/**
 * pat1 | pat2
 *
 * @property parsers
 */
class OrTree(vararg var parsers: Parser) : Element {
    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        extractNextSuccessParser(lexer)?.let {
            results.add(it.parseTokens(lexer))
        } ?: run {
            throw ParseException(lexer.readTokenAt(0))
        }
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return extractNextSuccessParser(lexer) != null
    }

    private fun extractNextSuccessParser(lexer: Lexer): Parser? {
        return parsers.firstOrNull { it.judgeNextSuccessOrNot(lexer) }
    }

    // TODO 動作確認
    fun insert(parser: Parser) {
        parsers = arrayOf(parser) + parsers
    }
}