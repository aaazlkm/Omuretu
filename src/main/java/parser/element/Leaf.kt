package parser.element

import lexer.Lexer
import lexer.token.IdToken
import lexer.token.Token
import parser.ast.ASTLeaf
import parser.ast.ASTTree
import parser.exception.ParseException

open class Leaf(private vararg val patterns: String) : Element {
    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        val token = lexer.pickOutNewToken()
        if (validateToken(token, *patterns)) {
            addResult(results, token)
            return
        }

        if (patterns.isEmpty()) {
            throw ParseException(token)
        } else {
            throw ParseException("${patterns.contentDeepToString()} expected.", token)
        }
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        val token = lexer.readTokenAt(0)
        return validateToken(token, *patterns)
    }

    /**
     * Tokenが正しいか検証する
     * IdToken && patternsに含まれている
     *
     * @param token Token
     * @param patterns パターン
     * @return Boolean
     */
    private fun validateToken(token: Token, vararg patterns: String): Boolean {
        return if (token is IdToken) {
            patterns.firstOrNull { it == token.id } != null
        } else {
            false
        }
    }

    // Skip用に結果登録の処理は切り出している
    // FIXME あまりうまい実装方法だと思えないので修正する
    open fun addResult(results: MutableList<ASTTree>, token: Token) {
        results.add(ASTLeaf(token))
    }
}
