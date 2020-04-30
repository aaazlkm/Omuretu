package omuretu

import omuretu.ast.statement.NullStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.native.NativeFunctionEnvironmentFactory
import lexer.token.Token
import omuretu.environment.TypeEnvironmentImpl
import omuretu.environment.base.TypeEnvironment
import util.utility.CodeDialog
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.lang.Exception


object OmuretuRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        run(CodeDialog())
    }

    fun run(reader: Reader) {
        val parser = OmuretuParser()
        val typeEnvironment = TypeEnvironmentImpl()
        val variableEnvironment = NativeFunctionEnvironmentFactory.createBasedOn(GlobalVariableEnvironment(), typeEnvironment)

        val lexer = OmuretuLexer(reader)
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val t = parser.parse(lexer)
            if (t !is NullStatement) {
                t.lookupIdNamesLocation(variableEnvironment.idNameLocationMap)
                val type = t.checkType(typeEnvironment)
                val result = t.evaluate(variableEnvironment)
            }
        }
    }
}