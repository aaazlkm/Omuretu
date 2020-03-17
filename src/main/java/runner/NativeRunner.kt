package runner

import omuretu.Environment
import omuretu.NestedEnvironment
import omuretu.OmuretuLexer
import omuretu.native.NativeFunctionEnvironmentFactory
import omuretu.parser.FuncParser
import lexer.token.Token
import omuretu.parser.ClosureParser

object NativeRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        run(ClosureParser(), NativeFunctionEnvironmentFactory.create())
    }

    private fun run(bp: ClosureParser, env: Environment) {
        val lexer = OmuretuLexer(CodeDialog())
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val astTree = bp.parse(lexer)
            val result = astTree.evaluate(env)
            println("=> $result")
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
fib 15
print getCurrentTimeMillis() - t + "msec"
 */