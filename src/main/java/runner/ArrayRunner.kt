package runner

import omuretu.Environment
import omuretu.OmuretuLexer
import omuretu.native.NativeFunctionEnvironmentFactory
import lexer.token.Token
import omuretu.parser.ArrayParser

object ArrayRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        run(ArrayParser(), NativeFunctionEnvironmentFactory.create())
    }

    private fun run(bp: ArrayParser, env: Environment) {
        val lexer = OmuretuLexer(CodeDialog())
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val astTree = bp.parse(lexer)
            val result = astTree.evaluate(env)
            println("=> $result")
        }
    }
}

/**

 a = [2,3,4]
 print a[1]
 a[1] = "three"
 print "a[1]:" + a[1]
 b = [["one", 1],["two",2]]
 print b[1][0] + ":" + b[1][1]
 */