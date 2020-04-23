package parser.ast

import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap
import omuretu.exception.OmuretuException
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

    override fun compile(byteCodeStore: ByteCodeStore) {
        children.forEach {
            it.compile(byteCodeStore)
        }
    }

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("not override evaluate method")
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append('(')
        var sep = ""
        for (t in children) {
            builder.append(sep)
            sep = " "
            builder.append(t.toString())
        }
        return builder.append(')').toString()
    }
}