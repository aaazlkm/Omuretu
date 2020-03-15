package ast

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import parser.ASTTreeFactory

class Name(
        override val token: IdToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTLeaf? {
            return if (argument is IdToken) {
                Name(argument)
            } else {
                null
            }
        }
    }
}