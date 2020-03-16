package omuretu.ast

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import parser.Environment
import parser.ast.ASTList
import parser.ast.ASTTree

class WhileStmnt(
        val condition: ASTTree,
        val body: ASTTree
) : ASTList(listOf(condition, body)) {
    companion object Factory: FactoryMethod {
        const val KEYWORD_WHILE = "while"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            return WhileStmnt(argument[0], argument[1])
        }
    }

    override fun evaluate(environment: Environment): Any {
        var bodyResult: Any = OMURETU_DEFAULT_RETURN_VALUE
        while (true) {
            val conditionResult = condition.evaluate(environment)
            if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
                bodyResult = body.evaluate(environment)
            } else {
                return bodyResult
            }
        }
    }

    override fun toString(): String {
        return "($KEYWORD_WHILE $condition $body)"
    }
}
