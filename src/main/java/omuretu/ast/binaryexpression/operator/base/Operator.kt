package omuretu.ast.binaryexpression.operator.base

import omuretu.environment.Environment
import parser.ast.ASTTree

interface Operator {
    val leftTree: ASTTree

    val rightTree: ASTTree

    val environment: Environment

    /**
     * TODO
     *
     * @param key
     * @param value
     * @param environment
     * @return
     */
    fun calculate(): Any
}