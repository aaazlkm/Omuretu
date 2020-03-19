package omuretu.ast

import omuretu.Environment
import omuretu.model.Object
import omuretu.ast.postfix.Postfix
import omuretu.exception.OmuretuException
import parser.ast.ASTList
import parser.ast.ASTTree

class PrimaryExpression(
        val literal: ASTTree,
        val postFixes: List<Postfix>
) : ASTList(listOf(literal) + postFixes) {
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

    // postFixesの最初の要素はDotやArgumentが来る
    // 上記の`newInstance`メソッドから、このメソッドが呼ばれる時必ず`postFixes`は要素を持つ
    val firstPostFix: Postfix
        get() = postFixes.first()

    // 上記の`newInstance`メソッドから、このメソッドが呼ばれる時必ず`postFixes`が要素を持つ
    override fun evaluate(environment: Environment): Any {
        var result: Any = literal.evaluate(environment)
        postFixes.forEach {
            result = it.evaluate(environment, result)
        }
        return result
    }

    fun obtainObject(environment: Environment): Any {
        var result: Any = literal.evaluate(environment)
        postFixes.subList(0, (postFixes.size - 1)).forEach {
            result = it.evaluate(environment, result)
        }
        return result
    }
}
