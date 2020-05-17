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

class ClassBodyStatement(
    val members: List<ASTTree>
) : ASTList(members) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACES_START = "{"
        const val KEYWORD_BRACES_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ClassBodyStatement(argument)
        }
    }

    override fun toString() = "$KEYWORD_BRACES_START $members $KEYWORD_BRACES_END"

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
