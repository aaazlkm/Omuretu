package parser.ast

import omuretu.Environment

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

    override fun evaluate(environment: Environment): Any {
        TODO("not implemented")
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