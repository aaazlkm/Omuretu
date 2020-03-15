package parser.ast

import parser.ASTTreeFactory

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