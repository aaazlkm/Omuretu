package omuretu.ast.statement

import omuretu.ast.listeral.IdNameLiteral
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

class ClassStatement(
    private val idNameLiteral: IdNameLiteral,
    private val superClassIdNameLiteral: IdNameLiteral? = null,
    val bodyStatement: ClassBodyStatement
) : ASTList(if (superClassIdNameLiteral == null) listOf(idNameLiteral, bodyStatement) else listOf(idNameLiteral, superClassIdNameLiteral, bodyStatement)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLASS = "class"
        const val KEYWORD_EXTENDS = "extends"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            when (argument.size) {
                2 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[1] as? ClassBodyStatement ?: return null
                    return ClassStatement(nameLiteral, null, bodyStmnt)
                }
                3 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val extendLiteral = argument[1] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[2] as? ClassBodyStatement ?: return null
                    return ClassStatement(nameLiteral, extendLiteral, bodyStmnt)
                }
                else -> {
                    return null
                }
            }
        }
    }

    val name: String
        get() = idNameLiteral.name

    val superClassName: String?
        get() = superClassIdNameLiteral?.name

    override fun toString() = "$KEYWORD_CLASS $idNameLiteral $KEYWORD_EXTENDS $superClassIdNameLiteral $bodyStatement"

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {}

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }
}
