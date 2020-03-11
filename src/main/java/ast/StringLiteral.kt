package ast

import parser.ast.ASTLeaf
import lexer.token.StringToken

class StringLiteral(override val token: StringToken) : ASTLeaf(token) {
    val string: String
        get() = token.string
}