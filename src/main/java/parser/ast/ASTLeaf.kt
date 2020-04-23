package parser.ast

import lexer.token.Token
import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap
import omuretu.exception.OmuretuException
import omuretu.vertualmachine.ByteCodeStore

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

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {}

    override fun compile(byteCodeStore: ByteCodeStore) {
        throw OmuretuException("not override compile method")
    }

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("not override evaluate method")
    }

    override fun toString(): String = token.toString()
}