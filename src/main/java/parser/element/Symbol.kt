package parser.element

import parser.exception.ParseException
import lexer.Lexer
import lexer.token.IdToken
import lexer.token.NumberToken
import lexer.token.StringToken
import lexer.token.Token
import parser.ASTTreeFactory
import parser.ast.ASTLeaf
import parser.ast.ASTTree
import java.util.HashSet

abstract class Symbol constructor(type: Class<out ASTLeaf>?) : Element {
    private val factory: ASTTreeFactory = if (type == null) {
        ASTTreeFactory.createInstance()
    } else {
        ASTTreeFactory.createInstance(type, ASTLeaf.argumentType)
    }

    override fun parseTokens(lexer: Lexer, results: MutableList<ASTTree>) {
        val token = lexer.pickOutNewToken()
        if (validateToken(token)) {
            val astLeaf = factory.makeASTTree(token)
            results.add(astLeaf)
        } else {
            throw ParseException(token)
        }
    }

    override fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return validateToken(lexer.readTokenAt(0))
    }

    abstract fun validateToken(token: Token): Boolean
}

/**
 * IDENTIFIER
 *
 * @param type
 */
class IdSymbol constructor(type: Class<out ASTLeaf>?, private val reserved: HashSet<String>) : Symbol(type) {
    override fun validateToken(token: Token): Boolean {
        return (token is IdToken) && !reserved.contains(token.id)
    }
}

/**
 * NUMBER
 *
 * @param type
 */
class NumberSymbol constructor(type: Class<out ASTLeaf>?) : Symbol(type) {
    override fun validateToken(token: Token): Boolean {
        return token is NumberToken
    }
}

/**
 * STRING
 *
 * @param type
 */
class StringSymbol constructor(type: Class<out ASTLeaf>?) : Symbol(type) {
    override fun validateToken(token: Token): Boolean {
        return token is StringToken
    }
}