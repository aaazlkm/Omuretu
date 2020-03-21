package runner

import omuretu.OmuretuLexer
import omuretu.parser.ArrayParser
import lexer.token.Token
import omuretu.environment.GlobalEnvironment
import omuretu.ast.statement.NullStmnt
import omuretu.native.NativeFunctionEnvironmentFactory

object EnvOptRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val environment = GlobalEnvironment()
        run(ArrayParser(), NativeFunctionEnvironmentFactory.createBasedOn(environment))
    }

    private fun run(bp: ArrayParser, environment: GlobalEnvironment) {
        val lexer = OmuretuLexer(CodeDialog())
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val t = bp.parse(lexer)
            if (t !is NullStmnt) {
                t.lookupIdNamesLocation(environment.idNameLocationMap)
                val r = t.evaluate(environment)
                println("=> $r")
            }
        }
    }
}

/**
def fib (n) {
if n < 2 {
n
} else {
fib(n - 1) + fib(n - 2)
}
}

t = getCurrentTimeMillis()
fib 32
print getCurrentTimeMillis() - t + "msec"
 */