package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class ParameterStatement(
    val idNameLiteral: IdNameLiteral,
    val typeStatement: TypeStatement
) : ASTList(listOf(idNameLiteral, typeStatement)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val idNameLiteral = argument[0] as? IdNameLiteral ?: return null
            val typeTag = argument[1] as? TypeStatement ?: return null
            return ParameterStatement(idNameLiteral, typeTag)
        }
    }

    val name: String
        get() = idNameLiteral.name

    val type: Type.Defined
        get() = typeStatement.type as Type.Defined // パラメータ型は事前に決まっている

    override fun toString() = "$idNameLiteral $typeStatement"

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any = OMURETU_DEFAULT_RETURN_VALUE
}
