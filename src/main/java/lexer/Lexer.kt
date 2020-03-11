package lexer

import lexer.token.Token

interface Lexer {
    /**
     * 新しいトークンを取り出す
     *
     * @return Token
     */
    fun pickOutNewToken(): Token

    /**
     * 現在のトークンからi個先のトークンを読み込む
     *
     * @param i
     * @return Token
     */
    fun readTokenAt(i: Int): Token
}