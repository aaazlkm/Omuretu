package runner

import omuretu.environment.Environment
import omuretu.OmuretuLexer
import omuretu.parser.FuncParser
import lexer.token.Token
import omuretu.environment.GlobalEnvironment


fun main(args: Array<String>) {
    val environment = GlobalEnvironment()
    run(FuncParser(), environment)
}

fun run(bp: FuncParser, env: Environment) {
    val lexer = OmuretuLexer(CodeDialog())
    while (lexer.readTokenAt(0) !== Token.EOF) {
        val astTree = bp.parse(lexer)
        val result = astTree.evaluate(env)
        println("=> $result")
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

fib(10)
 */