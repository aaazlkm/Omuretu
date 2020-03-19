package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.NumberToken
import lexer.token.Token
import omuretu.Environment
import parser.ast.ASTTree

class NumberLiteral(
        override val token: NumberToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return if (argument is NumberToken) {
                NumberLiteral(argument)
            } else {
                null
            }
        }
    }

    val value: Int
        get() = token.value

    override fun evaluate(environment: Environment): Any {
        return token.value
    }
}