package runner

import omuretu.Environment
import omuretu.NestedEnvironment
import omuretu.OmuretuLexer
import omuretu.parser.FuncParser
import lexer.token.Token



fun main(args: Array<String>) {
    run(FuncParser(), NestedEnvironment())
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