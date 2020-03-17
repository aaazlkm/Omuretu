package runner

import omuretu.Environment
import omuretu.parser.BasicParser
import omuretu.OmuretuLexer
import lexer.token.Token
import omuretu.BasicEnvironment

fun main(args: Array<String>) {
    run(BasicParser(), BasicEnvironment())
}

fun run(bp: BasicParser, env: Environment) {
    val lexer = OmuretuLexer(CodeDialog())
    while (lexer.readTokenAt(0) !== Token.EOF) {
        val astTree = bp.parse(lexer)
        val result = astTree.evaluate(env)
        println("=> $result")
    }
}