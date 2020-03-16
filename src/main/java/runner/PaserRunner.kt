package runner

import omuretu.OmuretuLexer
import omuretu.parser.BasicParser
import lexer.token.Token
import parser.ast.ASTList

object ParserRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val l = OmuretuLexer(CodeDialog())
        val bp = BasicParser()
        while (l.readTokenAt(0) !== Token.EOF) {
            val ast = bp.parse(l)

            println("=> " + (ast as? ASTList)?.toString() + "\n")
        }
    }
}