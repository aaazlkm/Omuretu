package omuretu.ast.statement

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.virtualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTList
import parser.ast.ASTTree

data class IfStatement(
    val conditionBlockStatements: List<ConditionBlockStatement>,
    val elseBlock: BlockStatement? = null
) : ASTList(elseBlock?.let { conditionBlockStatements.toMutableList<ASTList>().apply { add(it) } } ?: conditionBlockStatements) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_IF = "if"
        const val KEYWORD_ELSE = "else"
        const val KEYWORD_ELSEIF = "elseif"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.isEmpty()) return null
            return when (val last = argument.last()) {
                is BlockStatement -> {
                    IfStatement(argument.dropLast(1).map { it as ConditionBlockStatement }, last)
                }
                else -> {
                    IfStatement(argument.map { it as ConditionBlockStatement })
                }
            }
        }
    }

    var idNameSizeInElse: Int = 0

    override fun toString() = "($KEYWORD_IF ${conditionBlockStatements.first()} $KEYWORD_ELSEIF ${conditionBlockStatements.drop(1)} $KEYWORD_ELSE $elseBlock)"

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
