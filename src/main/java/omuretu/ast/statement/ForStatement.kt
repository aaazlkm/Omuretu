package omuretu.ast.statement

import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class ForStatement(
    val index: IdNameLiteral,
    val rangeStatement: RangeStatement,
    val blockStatement: BlockStatement
) : ASTList(listOf(rangeStatement, blockStatement)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_FOR = "for"
        const val KEYWORD_IN = "in"
        const val KEYWORD_PARENTHESIS_START = "("
        const val KEYWORD_PARENTHESIS_END = ")"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val index = argument[0] as? IdNameLiteral ?: return null
            val rangeStatement = argument[1] as? RangeStatement ?: return null
            val blockStatement = argument[2] as? BlockStatement ?: return null
            return ForStatement(index, rangeStatement, blockStatement)
        }
    }

    var idNameSize: Int = 0

    override fun toString() = "($KEYWORD_FOR $KEYWORD_PARENTHESIS_START $index $KEYWORD_IN $rangeStatement $KEYWORD_PARENTHESIS_END $blockStatement)"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
