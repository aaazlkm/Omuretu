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

data class WhileStatement(
        val condition: ASTTree,
        val body: BlockStatement
) : ASTList(listOf(condition, body)) {
    companion object Factory: FactoryMethod {
        const val KEYWORD_WHILE = "while"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val body = argument[1] as? BlockStatement ?: return null
            return WhileStatement(argument[0], body)
        }
    }

    override fun toString() = "$KEYWORD_WHILE $condition $body"

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
