package runner

import parser.Environment
import omuretu.parser.basic.BasicEnvironment
import omuretu.parser.basic.BasicParser
import omuretu.OmuretuLexer
import lexer.token.Token

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