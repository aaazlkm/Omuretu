package ast

import parser.ast.ASTLeaf
import lexer.token.NumberToken

class NumberLiteral(override val token: NumberToken) : ASTLeaf(token) {
    val value: Int
        get() = token.value
}