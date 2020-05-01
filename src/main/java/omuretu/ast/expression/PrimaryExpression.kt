package omuretu.ast.expression

import omuretu.ast.postfix.Postfix
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class PrimaryExpression(
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

    fun obtainObject(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        var result: Any = literal.accept(evaluateVisitor, variableEnvironment)
        postFixes.subList(0, (postFixes.size - 1)).forEach {
            result = it.accept(evaluateVisitor, variableEnvironment, result)
        }
        return result
    }

    override fun toString() = "$literal $postFixes"

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {
        compileVisitor.visit(this, byteCodeStore)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
