package omuretu.ast.postfix

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

data class ArgumentPostfix(
        val astTrees: List<ASTTree>
) : Postfix(astTrees) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"
        const val KEYWORD_ARGUMENT_BREAK = ","

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ArgumentPostfix(argument)
        }
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        return checkTypeVisitor.visit(this, typeEnvironment, leftType)
    }

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {
        return compileVisitor.visit(this, byteCodeStore)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return evaluateVisitor.visit(this, variableEnvironment, leftValue)
    }
}