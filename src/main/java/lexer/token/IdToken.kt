package lexer.token

class IdToken(lineNumber: Int, val id: String) : Token(lineNumber) {
    companion object {
        /** end of line */
        const val EOL = "\\n"
    }
}