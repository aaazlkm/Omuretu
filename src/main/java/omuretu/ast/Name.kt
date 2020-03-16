package omuretu.ast

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import omuretu.exception.OmuretuException
import parser.Environment

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

    override fun evaluate(environment: Environment): Any {
        return environment.get(token.id) ?: throw OmuretuException("undefined name: ${token.id}", this)
    }
}