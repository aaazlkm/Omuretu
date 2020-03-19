package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import omuretu.exception.OmuretuException
import omuretu.Environment

class NameLiteral(
        override val token: IdToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTLeaf? {
            return if (argument is IdToken) {
                NameLiteral(argument)
            } else {
                null
            }
        }
    }

    val name: String
        get() = token.id

    override fun evaluate(environment: Environment): Any {
        return environment.get(token.id) ?: throw OmuretuException("undefined name: ${token.id}", this)
    }
}