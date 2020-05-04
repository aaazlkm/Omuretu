package omuretu.ast.statement

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class ConditionBlockStatement(
    val condition: ASTList,
    val blockStatement: BlockStatement
) : ASTList(listOf(condition, blockStatement)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val condition = argument[0] as? ASTList ?: return null
            val blockStatement = argument[1] as? BlockStatement ?: return null
            return ConditionBlockStatement(condition, blockStatement)
        }
    }

    var idNameSizeInBlock: Int = 0

    override fun toString() = "$KEYWORD_PARENTHESIS_START $condition $KEYWORD_PARENTHESIS_END $blockStatement"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }
}
