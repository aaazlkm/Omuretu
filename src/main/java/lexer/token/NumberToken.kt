package lexer.token

class NumberToken(lineNumber: Int, val value: Int) : Token(lineNumber) {
    override fun toString(): String = value.toString()
}