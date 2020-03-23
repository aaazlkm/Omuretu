package runner

import omuretu.OmuretuLexer
import omuretu.ast.statement.NullStmnt
import omuretu.environment.GlobalEnvironment
import omuretu.native.NativeFunctionEnvironmentFactory
import omuretu.parser.ArrayParser
import lexer.token.Token

object ObjOptRunner {
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
class Position {
x = y = 0

def move (px, py) {
x = px
y = py
}
}

class Position1 extends Position {
z = 0

def move(px, py, pz) {
x = px
y = py
z = pz
}
}
p = Position1.new
p.move(3,4)
p.move(1,1,5)
p.x = 10
print p.x
print p.y
print p.z

 */