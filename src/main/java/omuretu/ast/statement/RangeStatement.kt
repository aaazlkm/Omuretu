package omuretu.ast.statement

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class RangeStatement(
    val from: ASTTree,
    val to: ASTTree,
    val step: ASTTree? = null
) : ASTList(listOf(from, to)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_RANGE = "to"
        const val KEYWORD_STEP = "step"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return when (argument.size) {
                2 -> RangeStatement(argument[0], argument[1])
                3 -> RangeStatement(argument[0], argument[1], argument[2])
                else -> null
            }
        }
    }

    data class EvaluatedValue(
        val from: Int,
        val to: Int,
        val step: Int = 1
    )

    override fun toString() = "$from $KEYWORD_RANGE $to $KEYWORD_STEP $step"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type = Type.Defined.Range()

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
