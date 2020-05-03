package omuretu.ast.statement

import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
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

data class DefStatement(
    val idNameLiteral: IdNameLiteral,
    val parameters: ParametersStatement,
    val typeStatement: TypeStatement,
    val blockStatement: BlockStatement
) : ASTList(listOf(idNameLiteral, parameters, blockStatement)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_DEF = "def"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 4) return null
            val name = argument[0] as? IdNameLiteral ?: return null
            val parameters = argument[1] as? ParametersStatement ?: return null
            val typeTag = argument[2] as? TypeStatement ?: return null
            val blockStmnt = argument[3] as? BlockStatement ?: return null
            return DefStatement(name, parameters, typeTag, blockStmnt)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    var idNamesInDefSize: Int? = null

    override fun toString() = "$KEYWORD_DEF $idNameLiteral $parameters $blockStatement"

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
