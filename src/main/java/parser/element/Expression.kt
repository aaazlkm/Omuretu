package parser.element

import java.util.ArrayList
import java.util.HashMap
import lexer.Lexer
import lexer.token.IdToken
import parser.ASTTreeFactory
import parser.Parser
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree

class Expression constructor(
    clazz: Class<out ASTTree>?,
    var parser: Parser,
    var operators: Operators
) : Element {
    class Operators : HashMap<String, Precedence>() {
        fun add(name: String, precedence: Int, assoc: Precedence.Assoc) {
            put(name, Precedence(precedence, assoc))
        }
    }

    class Precedence(val value: Int, val assoc: Assoc) {
        enum class Assoc { LEFT, RIGHT }
    }

    private val factory: ASTTreeFactory = if (clazz == null) {
        ASTTreeFactory.createInstance()
    } else {
        ASTTreeFactory.createInstance(clazz, ASTList.argumentType)
    }

    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        var astTree = parser.parseTokens(lexer)
        var precedence: Precedence
        while (true) {
            precedence = getNextOperator(lexer) ?: break
            astTree = doShift(lexer, astTree, precedence.value)
        }

        results.add(astTree)
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return parser.judgeNextSuccessOrNot(lexer)
    }

    private fun doShift(lexer: Lexer, left: ASTTree, precedence: Int): ASTTree {
        val astTrees = ArrayList<ASTTree>()
        astTrees.add(left)
        astTrees.add(ASTLeaf(lexer.pickOutNewToken()))
        var right = parser.parseTokens(lexer)
        var nextPrecedence: Precedence
        while (true) {
            nextPrecedence = getNextOperator(lexer) ?: break
            if (!rightIsExpression(precedence, nextPrecedence)) break
            right = doShift(lexer, right, nextPrecedence.value)
        }
        astTrees.add(right)

        return factory.makeASTTree(astTrees)
    }

    private fun getNextOperator(lexer: Lexer): Precedence? {
        val token = lexer.readTokenAt(0)
        return if (token is IdToken) {
            operators[token.id]
        } else {
            null
        }
    }

    private fun rightIsExpression(precedence: Int, nextPrecedence: Precedence): Boolean {
        return when (nextPrecedence.assoc) {
            Precedence.Assoc.LEFT -> precedence < nextPrecedence.value
            Precedence.Assoc.RIGHT -> precedence <= nextPrecedence.value
        }
    }
}
