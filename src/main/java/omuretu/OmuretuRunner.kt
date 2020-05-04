package omuretu

import java.io.Reader
import lexer.token.Token
import omuretu.ast.statement.NullStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.TypeEnvironmentImpl
import omuretu.native.NativeFunctionEnvironmentFactory
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import util.utility.CodeDialog

object OmuretuRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        run(CodeDialog())
    }

    fun run(reader: Reader) {
        val parser = OmuretuParser()
        val typeEnvironment = TypeEnvironmentImpl()
        val variableEnvironment = NativeFunctionEnvironmentFactory.createBasedOn(GlobalVariableEnvironment(), typeEnvironment)

        val idNameLocationVisitor = IdNameLocationVisitor()
        val checkTypeVisitor = CheckTypeVisitor()
        val evaluateVisitor = EvaluateVisitor()

        val lexer = OmuretuLexer(reader)
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val t = parser.parse(lexer)
            if (t !is NullStatement) {
                t.accept(idNameLocationVisitor, variableEnvironment.idNameLocationMap)
                val type = t.accept(checkTypeVisitor, typeEnvironment)
                val result = t.accept(evaluateVisitor, variableEnvironment)
                println("$result: $type")
            }
        }
    }
}
