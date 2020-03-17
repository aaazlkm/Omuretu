package omuretu.ast

import omuretu.Environment
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.ast.postfix.Postfix
import parser.ast.ASTList
import parser.ast.ASTTree

class PrimaryExpression(
        val literal: ASTTree,
        val postFixes: List<Postfix>
) : ASTList(listOf()) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return if (argument.size == 1) {
                argument[0]
            } else {
                val primaryExpressionArguments = argument.subList(1, argument.size).mapNotNull { it as? Postfix }
                if (primaryExpressionArguments.size != (argument.size - 1)) return null
                PrimaryExpression(argument[0], primaryExpressionArguments)
            }
        }
    }

    // 上記の`newInstance`メソッドから、このメソッドが呼ばれる時必ず`postFixes`が要素を持つ
    override fun evaluate(environment: Environment): Any {
        var result: Any = literal.evaluate(environment)
        postFixes.forEach {
            result = it.evaluate(environment, literal.evaluate(environment))
        }
        return result
    }
}
