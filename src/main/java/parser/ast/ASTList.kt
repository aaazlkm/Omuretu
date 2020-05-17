package parser.ast

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.virtualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor

open class ASTList(val children: List<ASTTree>) : ASTTree {
    interface FactoryMethod {
        fun newInstance(argument: List<ASTTree>): ASTTree?
    }

    companion object : FactoryMethod {
        val argumentType = List::class.java

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ASTList(argument)
        }
    }

    val numberOfChildren: Int
        get() = children.size

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        children.forEach {
            it.accept(idNameLocationVisitor, idNameLocationMap)
        }
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type = throw OmuretuException("not override checkType method")

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {
        children.forEach {
            it.accept(compileVisitor, byteCodeStore)
        }
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any = throw OmuretuException("not override evaluate method")

    override fun toString(): String {
        return children.map { it.toString() }.fold("") { acc, s -> "$acc$s" }
    }
}
