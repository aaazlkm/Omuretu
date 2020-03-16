package omuretu.ast

import parser.ast.ASTLeaf
import lexer.token.StringToken
import lexer.token.Token
import parser.Environment
import parser.ast.ASTTree

class StringLiteral(
        override val token: StringToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return if (argument is StringToken) {
                StringLiteral(argument)
            } else {
                null
            }
        }
    }

    val string: String
        get() = token.string

    override fun evaluate(environment: Environment): Any {
        return token.string
    }
}