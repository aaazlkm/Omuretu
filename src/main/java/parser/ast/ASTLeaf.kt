package parser.ast

import lexer.token.Token
import omuretu.environment.base.VariableEnvironment
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor

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

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        throw OmuretuException("not override IdNameLocationVisitor method $this")
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        throw OmuretuException("not override CheckTypeVisitor method $this")
    }

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {
        throw OmuretuException("not override CompileVisitor method $this")
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("not override EvaluateVisitor method $this")
    }

    override fun toString(): String = token.toString()
}