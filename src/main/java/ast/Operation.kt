package ast

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import parser.ASTTreeFactory
import parser.ast.ASTTree

class Operation(
        override val token: IdToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return if (argument is IdToken) {
                Operation(argument)
            } else {
                null
            }
        }
    }
}