package parser.ast

import omuretu.Environment

interface ASTTree {
    fun evaluate(environment: Environment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
