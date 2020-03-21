package runner

import omuretu.environment.Environment
import omuretu.parser.BasicParser
import omuretu.OmuretuLexer
import lexer.token.Token
import omuretu.environment.GlobalEnvironment

fun main(args: Array<String>) {
    val environment = GlobalEnvironment()
    run(BasicParser(), environment)
}

fun run(bp: BasicParser, env: Environment) {
    val lexer = OmuretuLexer(CodeDialog())
    while (lexer.readTokenAt(0) !== Token.EOF) {
        val astTree = bp.parse(lexer)
        val result = astTree.evaluate(env)
        println("=> $result")
    }
}