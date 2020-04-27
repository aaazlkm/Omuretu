package parser.ast

import omuretu.environment.base.VariableEnvironment
import omuretu.NestedIdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore

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

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        children.forEach {
            it.lookupIdNamesLocation(idNameLocationMap)
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type = throw OmuretuException("not override checkType method")

    override fun compile(byteCodeStore: ByteCodeStore) {
        children.forEach {
            it.compile(byteCodeStore)
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("not override evaluate method")
    }

    override fun toString(): String {
        return children.map { it.toString() }.fold("") { acc, s -> "$acc$s" }
    }
}