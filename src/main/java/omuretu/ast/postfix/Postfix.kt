package omuretu.ast.postfix

import omuretu.Environment
import parser.ast.ASTList
import parser.ast.ASTTree

abstract class Postfix(children: List<ASTTree>): ASTList(children) {
    companion object {
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"
    }

    abstract fun evaluate(environment: Environment, leftValue: Any): Any
}