package chapter3

import OmuretuLexer
import lexer.token.IdToken
import lexer.token.NumberToken
import lexer.token.StringToken
import lexer.token.Token
import java.io.FileNotFoundException

class FileLexerRunner {
    fun main(args: Array<String>) {
        try {
            val lexer = OmuretuLexer(CodeDialog.file())
            var token: Token
            while (true) {
                token = lexer.pickOutNewToken()
                if (token === Token.EOF) break
                System.out.println("=> $token")
            }
        } catch (e: FileNotFoundException) {
            println(e.message)
        }
    }
}

fun main(args: Array<String>) {
    val lexer = OmuretuLexer(CodeDialog())
    var token: Token
    while (true) {
        token = lexer.pickOutNewToken()
        if (token === Token.EOF) break
        val log  = when(token) {
            is IdToken -> {
                "IdToken: id: ${token.id} linenumber: ${token.lineNumber}"
            }
            is NumberToken -> {
                "NumberToken: value: ${token.value} linenumber: ${token.lineNumber}"
            }
            is StringToken -> {
                "StringToken: string: ${token.string} linenumber: ${token.lineNumber}"
            }
            else -> ""
        }
        println("=> $log")
    }
}