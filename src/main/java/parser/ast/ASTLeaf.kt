package parser.ast

import lexer.token.Token
import omuretu.Environment

open class ASTLeaf(open val token: Token) : ASTTree {
    interface FactoryMethod {
        fun newInstance(argument: Token): ASTTree?
    }

    companion object Factory: FactoryMethod {
        val argumentType = Token::class.java

        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return ASTLeaf(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        TODO("not implemented")
    }

    override fun toString(): String = token.toString()
}