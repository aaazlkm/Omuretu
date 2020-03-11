package exception

import lexer.token.Token
import java.io.IOException

class ParseException : Exception {
    constructor(e: IOException) : super(e) {}
    constructor(msg: String) : super(msg) {}
    constructor(token: Token) : super("token: $token, at line: ${token.lineNumber}")
    constructor(msg: String, token: Token) : super("syntax error around " + location(token) + ". " + msg)

    companion object {
        private fun location(token: Token): String {
            return if (token == Token.EOF) {
                "the last line"
            } else {
                "token: $token  at line ${token.lineNumber}"
            }
        }
    }
}
