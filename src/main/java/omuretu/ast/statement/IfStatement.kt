package omuretu.ast.statement

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class IfStatement(
        val condition: ASTTree,
        val thenBlock: BlockStatement,
        val elseBlock: BlockStatement? = null
) : ASTList(elseBlock?.let { listOf(condition, thenBlock, it) } ?: listOf(condition, thenBlock)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_IF = "if"
        const val KEYWORD_ELSE = "else"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 2..3) return null
            val thenBlock = argument[1] as? BlockStatement ?: return null
            return when (argument.size) {
                2 -> IfStatement(argument[0], thenBlock)
                3 -> {
                    val elseBlock = argument[2] as? BlockStatement ?: return null
                    IfStatement(argument[0], thenBlock, elseBlock)
                }
                else -> null
            }
        }
    }

    override fun toString() = "($KEYWORD_IF $condition $thenBlock $KEYWORD_ELSE $elseBlock)"

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