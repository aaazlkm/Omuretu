package lexer.token

class StringToken(lineNumber: Int, val string: String) : Token(lineNumber) {
    override fun toString(): String = string
}
