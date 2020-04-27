package omuretu.ast

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.postfix.Postfix
import omuretu.environment.base.TypeEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
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

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        var result = literal.checkType(typeEnvironment)
        postFixes.forEach {
            result = it.checkType(typeEnvironment, result)
        }
        return result
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        literal.compile(byteCodeStore)
        postFixes.forEach {
            it.compile(byteCodeStore)
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        var result: Any = literal.evaluate(variableEnvironment)
        postFixes.forEach {
            result = it.evaluate(variableEnvironment, result)
        }
        return result
    }

    fun obtainObject(variableEnvironment: VariableEnvironment): Any {
        var result: Any = literal.evaluate(variableEnvironment)
        postFixes.subList(0, (postFixes.size - 1)).forEach {
            result = it.evaluate(variableEnvironment, result)
        }
        return result
    }
}
