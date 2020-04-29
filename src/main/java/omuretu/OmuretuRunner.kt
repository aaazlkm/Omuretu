package omuretu

import omuretu.ast.statement.NullStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.native.NativeFunctionEnvironmentFactory
import lexer.token.Token
import omuretu.environment.TypeEnvironmentImpl
import omuretu.environment.base.TypeEnvironment
import util.utility.CodeDialog

object OmuretuRunner {
    @JvmStatic
    fun main(args: Array<String>) {
        val typeEnvironment = TypeEnvironmentImpl()
        val variableEnvironment = NativeFunctionEnvironmentFactory.createBasedOn(GlobalVariableEnvironment(), typeEnvironment)
        run(
                OmuretuParser(),
                typeEnvironment,
                variableEnvironment
        )
    }

    private fun run(bp: OmuretuParser, typeEnvironment: TypeEnvironment, environment: GlobalVariableEnvironment) {
        val lexer = OmuretuLexer(CodeDialog())
        while (lexer.readTokenAt(0) !== Token.EOF) {
            val t = bp.parse(lexer)
            if (t !is NullStatement) {
                t.lookupIdNamesLocation(environment.idNameLocationMap)
                val type = t.checkType(typeEnvironment)
                val result = t.evaluate(environment)
                println("=> $result : $type")
            }
        }
    }
}

/**
 *
def fact(n: Int): Int {
if n > 1 { n * fact(n - 1) } else { 1 }
}

fact 5
 */

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

---------------------------------------------------------------------

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

/**
class Fib {
fib0 = 0
fib1 = 1
a = 0

def fib (n) {
a = n
if n == 0 {
fib0
} else {
if (n == 1) {
this.fib1
} else {
fib(n - 1) + this.fib(n - 2)
}
}
}
}

t = getCurrentTimeMillis()
f = Fib.new
f.fib 10
print getCurrentTimeMillis() - t + "msec"

3194
 */