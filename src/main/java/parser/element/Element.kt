package parser.element

import lexer.Lexer
import parser.ast.ASTTree

interface Element {
    /**
     * TODO
     *
     * @param lexer
     * @param results
     */
    fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>)

    /**
     * 次のトークンを読み取って構文解析が成功するか判断する
     *
     * @param lexer Lexer
     * @return Boolean
     */
    fun judgeNextSuccessOrNot(lexer: Lexer): Boolean
}