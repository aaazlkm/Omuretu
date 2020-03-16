package parser.ast

import parser.Environment

interface ASTTree {
    fun evaluate(environment: Environment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
