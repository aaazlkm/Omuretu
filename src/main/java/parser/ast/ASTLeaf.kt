package parser.ast

import lexer.token.Token
import omuretu.environment.base.VariableEnvironment
import omuretu.environment.NestedIdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
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

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        // TODO 何もしなくていいのか調査
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        throw OmuretuException("not override checkType method $this")
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        throw OmuretuException("not override compile method $this")
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("not override evaluate method $this")
    }

    override fun toString(): String = token.toString()
}