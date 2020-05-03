package lexer.token
open class Token(
    val lineNumber: Int
) {
    companion object {
        // TODO こいつ用に新しいTokenを定義してあげたほうがいいかも
        /** end of file */
        val EOF = Token(-1)
    }

    override fun toString(): String = "linenumber: $lineNumber, token: $this"
}
