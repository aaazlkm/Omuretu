package parser

import lexer.Lexer
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree
import parser.element.*
import java.util.HashSet

class Parser {
    companion object {
        fun rule(clazz: Class<out ASTList>? = null): Parser {
            return Parser(clazz)
        }
    }

    private var elements: MutableList<Element> = mutableListOf()
    private var factory: ASTTreeFactory

    constructor(clazz: Class<out ASTList>? = null) {
        this.factory = if (clazz == null) {
            ASTTreeFactory.createInstance()
        } else {
            ASTTreeFactory.createInstance(clazz, ASTList.argumentType)
        }
    }

    constructor(parser: Parser) {
        this.elements = parser.elements
        this.factory = parser.factory
    }

    fun parseTokens(lexer: Lexer): ASTTree {
        val results = mutableListOf<ASTTree>()
        elements.forEach {
            it.parseTokens(lexer, results)
        }

        return factory.makeASTTree(results)
    }

    fun judgeNextSuccessOrNot(lexer: Lexer): Boolean {
        return if (elements.size == 0) {
            true
        } else {
            elements[0].judgeNextSuccessOrNot(lexer)
        }
    }

    fun reset(): Parser {
        this.elements.clear()
        return this
    }

    fun reset(clazz: Class<out ASTList>?): Parser {
        this.elements.clear()
        this.factory = if (clazz == null) {
            ASTTreeFactory.createInstance()
        } else {
            ASTTreeFactory.createInstance(clazz, ASTList.argumentType)
        }
        return this
    }

    fun insertChoice(parser: Parser): Parser {
        val element = elements.firstOrNull()
        if (element is OrTree) {
            element.insert(parser)
        } else {
            val otherwise = Parser(this).apply {
                reset(null)
            }
            elements.add(OrTree(parser, otherwise))
        }
        return this
    }

    /**
     * TODO
     *
     * @param clazz
     * @return
     */
    fun number(clazz: Class<out ASTLeaf>? = null): Parser {
        elements.add(NumberSymbol(clazz))
        return this
    }

    /**
     * TODO
     *
     * @param clazz
     * @param reserved
     * @return
     */
    fun identifier(reserved: HashSet<String>, clazz: Class<out ASTLeaf>? = null): Parser {
        elements.add(IdSymbol(clazz, reserved))
        return this
    }

    /**
     * TODO
     *
     * @param clazz
     * @return
     */
    fun string(clazz: Class<out ASTLeaf>? = null): Parser {
        elements.add(StringSymbol(clazz))
        return this
    }

    /**
     * TODO
     *
     * @param pat
     * @return
     */
    fun token(vararg pat: String): Parser {
        elements.add(Token(*pat))
        return this
    }

    /**
     * TODO
     *
     * @param pat
     * @return
     */
    fun sep(vararg pat: String): Parser {
        elements.add(Skip(*pat))
        return this
    }

    /**
     * TODO
     *
     * @param parser
     * @return
     */
    fun ast(parser: Parser): Parser {
        elements.add(Tree(parser))
        return this
    }

    /**
     * TODO
     *
     * @param parsers
     * @return
     */
    fun or(vararg parsers: Parser): Parser {
        elements.add(OrTree(*parsers))
        return this
    }

    /**
     * TODO
     *
     * @param parser
     * @return
     */
    fun maybe(parser: Parser): Parser {
        val p2 = Parser(parser)
        p2.reset()
        elements.add(OrTree(parser, p2))
        return this
    }

    /**
     * TODO
     *
     * @param parser
     * @return
     */
    fun option(parser: Parser): Parser {
        elements.add(Option(parser))
        return this
    }

    /**
     * TODO
     *
     * @param parser
     * @return
     */
    fun repeat(parser: Parser): Parser {
        elements.add(Repeat(parser))
        return this
    }

    /**
     * TODO
     *
     * @param subExpression
     * @param operators
     * @param clazz
     * @return
     */
    fun expression(subExpression: Parser, operators: Expression.Operators, clazz: Class<out ASTTree>? = null): Parser {
        elements.add(Expression(clazz, subExpression, operators))
        return this
    }
}