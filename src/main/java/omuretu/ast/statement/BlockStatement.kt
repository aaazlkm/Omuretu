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

data class BlockStatement(
        val astTrees: List<ASTTree>
) : ASTList(astTrees) {
    companion object Factory : FactoryMethod {
        val BLOCK_START = "{"
        val BLOCK_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return BlockStatement(argument)
        }
    }

    override fun toString() = "$BLOCK_START $astTrees $BLOCK_END"

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
