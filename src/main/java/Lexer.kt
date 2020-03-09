import exception.ParseException
import token.IdToken
import token.NumberToken
import token.StringToken
import token.Token
import java.io.LineNumberReader
import java.io.Reader

/**
 * FIXME 日本語入力できない
 */
class Lexer(
        reader: Reader
) {
    companion object {
        private const val KEY_SPACE = "space"
        private const val KEY_COMMENT = "comment"
        private const val KEY_NUMBER = "number"
        private const val KEY_STRING = "string"
        private const val KEY_ID = "id"
        private const val KEY_CONTENT = "content"

        private const val REGEX_PAT_SPACE = """(?<$KEY_SPACE>\s*)"""
        private const val REGEX_PAT_COMMENT = "(?<$KEY_COMMENT>(//.*))"
        private const val REGEX_PAT_NUMBER = "(?<$KEY_NUMBER>([0-9]+))"
        private const val REGEX_PAT_STRING = """(?<$KEY_STRING>("(\\"|\\\\|\\n|[^"])*"))"""
        private const val REGEX_PAT_ID = """(?<$KEY_ID>[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\|\||\p{Punct})"""

        private const val REGEX_PAT = "$REGEX_PAT_SPACE(?<$KEY_CONTENT>($REGEX_PAT_COMMENT|$REGEX_PAT_NUMBER|$REGEX_PAT_STRING|$REGEX_PAT_ID))?"
        private val REGEX_PATTERN = Regex(REGEX_PAT).toPattern()
    }

    private val lineNumberReader = LineNumberReader(reader)
    private val tokenQueue = mutableListOf<Token>()

    /**
     * 読み込めるテキストが存在するかどうか
     * `TokenQueue`に`Token.EOF`が含まれていた時、読み込みは終了していることを意味する
     */
    private val existsReadableText: Boolean
        get() = !this.tokenQueue.contains(Token.EOF)

    /**
     * 新しいトークンを取り出す
     *
     * @return Token
     */
    fun takeOutNewToken(): Token {
        val token = readTokenAt(0)
        if (this.tokenQueue.isNotEmpty()) tokenQueue.removeAt(0)
        return token
    }

    /**
     * 現在のトークンからi個先のトークンを読み込む
     *
     * @param i
     * @return Token
     */
    fun readTokenAt(i: Int): Token {
        return tokenQueue.getOrNull(i)?.let { token ->
            token
        } ?: run {
            while (i >= this.tokenQueue.size) {
                if (existsReadableText) {
                    queueTokensInNextLine()
                } else {
                    break
                }
            }

            this.tokenQueue.getOrNull(i) ?: Token.EOF
        }
    }

    private fun queueTokensInNextLine() {
        // 終了している場合抜け出す
        if (!existsReadableText) return

        val tokens = mutableListOf<Token>()

        val lineNumber = lineNumberReader.lineNumber
        val line = lineNumberReader.readLine() ?: run {
            lineNumberReader.close()
            this.tokenQueue.addAll(listOf(Token.EOF))
            return
        }

        tokens.addAll(convertLineToTokens(lineNumber, line))
        tokens.add(IdToken(lineNumber, IdToken.EOL))

        this.tokenQueue.addAll(tokens)
    }

    private fun convertLineToTokens(lineNumber: Int, line: String): List<Token> {
        val tokens = mutableListOf<Token>()

        val matcher = REGEX_PATTERN.matcher(line)
                .useTransparentBounds(true)
                .useAnchoringBounds(false)

        var position = 0
        val endPosition = line.length
        while (position < endPosition) {
            matcher.region(position, endPosition)
            if (matcher.lookingAt()) {
                if (matcher.group(KEY_CONTENT) != null && matcher.group(KEY_COMMENT) == null) {
                    when {
                        matcher.group(KEY_NUMBER) != null -> NumberToken(lineNumber, Integer.parseInt(matcher.group(KEY_NUMBER)))
                        matcher.group(KEY_STRING) != null -> StringToken(lineNumber, parseToStringLiteral(matcher.group(KEY_STRING)))
                        matcher.group(KEY_ID) != null -> IdToken(lineNumber, matcher.group(KEY_ID))
                        else -> null
                    }?.let {
                        tokens.add(it)
                    }
                }
                position = matcher.end()
            } else {
                throw ParseException("bad token at line $lineNumber")
            }
        }

        return tokens
    }

    private fun parseToStringLiteral(string: String): String {
        val stringBuilder = StringBuilder()
        val length = string.length - 1
        var i = 1
        while (i < length) {
            var char = string[i]
            if (char == '\\' && i + 1 < length) {
                val charNext = string[i + 1].toInt()
                if (charNext == '"'.toInt() || charNext == '\\'.toInt()) { // [\" -> "], [\\ -> \] に変換
                    i++
                    char = string[i]
                } else if (charNext == 'n'.toInt()) { // [\n -> `\n`], に変換
                    i++
                    char = '\n'
                }
            }
            stringBuilder.append(char)
            i++
        }
        return stringBuilder.toString()
    }
}