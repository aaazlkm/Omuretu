package runner

import omuretu.environment.Environment
import omuretu.OmuretuLexer
import omuretu.native.NativeFunctionEnvironmentFactory
import omuretu.parser.ClassParser
import lexer.token.Token
import omuretu.environment.GlobalEnvironment

object ClassRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val environment = GlobalEnvironment()
        run(ClassParser(), NativeFunctionEnvironmentFactory.createBasedOn(environment))
    }

    private fun run(bp: ClassParser, env: Environment) {
        val lexer = OmuretuLexer(CodeDialog())
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val astTree = bp.parse(lexer)
            val result = astTree.evaluate(env)
            println("=> $result")
        }
    }
}
/**
class Position {
    x = y = 0

    def move (px, py) {
        x = px
        y = py
    }
}
p = Position.new
p.move(3,4)
p.x = 10
print p.x * p.y
 */