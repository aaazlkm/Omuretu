package chapter5

import chapter3.CodeDialog
import lexer.OmuretuLexer
import BasicParser
import lexer.token.IdToken
import lexer.token.Token

object ParserRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val l = OmuretuLexer(CodeDialog())
        val bp = BasicParser()
        while (l.readTokenAt(0) !== Token.EOF) {
            val ast = bp.parse(l)
            System.out.println("=> " + ast.toString())
        }
    }
}