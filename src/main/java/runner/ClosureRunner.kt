package runner

import omuretu.Environment
import omuretu.NestedEnvironment
import omuretu.OmuretuLexer
import omuretu.parser.ClosureParser
import lexer.token.Token

fun main(args: Array<String>) {
    run(ClosureParser(), NestedEnvironment())
}

fun run(bp: ClosureParser, env: Environment) {
    val lexer = OmuretuLexer(CodeDialog())
    while (lexer.readTokenAt(0) !== Token.EOF) {
        val astTree = bp.parse(lexer)
        val result = astTree.evaluate(env)
        println("=> $result")
    }
}

/**
inc = closure (x) { x + 1 }
inc(3)
 */