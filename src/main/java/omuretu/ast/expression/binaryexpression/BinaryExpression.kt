package omuretu.ast.expression.binaryexpression

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.model.InlineCache
import omuretu.typechecker.Type
import omuretu.virtualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree

data class BinaryExpression(
    val left: ASTTree,
    val operator: ASTLeaf,
    val right: ASTTree
) : ASTList(listOf(left, operator, right)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val operation = argument[1] as? ASTLeaf ?: return null
            return BinaryExpression(argument[0], operation, argument[2])
        }
    }

    var inlineCache: InlineCache? = null

    override fun toString() = "$left $operator $right"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

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
