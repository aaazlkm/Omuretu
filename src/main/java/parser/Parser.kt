package parser

import java.util.HashSet
import lexer.Lexer
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree
import parser.element.Element
import parser.element.Expression
import parser.element.IdSymbol
import parser.element.NumberSymbol
import parser.element.Option
import parser.element.OrTree
import parser.element.Repeat
import parser.element.Skip
import parser.element.StringSymbol
import parser.element.Token
import parser.element.Tree

/**
 * BNF文法を全て構文解析できるわけではなく、
 * 1つのトークンを先読みするだけで選択肢のどれを選ぶか一意に決定できる文法でなくてはならない
 * 非終端記号の文法法則の先頭は終端記号が異なる記号でなければならない
 */
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

    fun resetElements(): Parser {
        this.elements = mutableListOf()
        return this
    }

    /**
     * 整数リテラルを規則に追加する
     *
     * @param clazz
     * @return
     */
    fun number(clazz: Class<out ASTLeaf>): Parser {
        elements.add(NumberSymbol(clazz))
        return this
    }

    /**
     * 予約語を覗く識別子を規則に追加する
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
     * 文字列リテラルを規則に追加する
     *
     * @param clazz
     * @return
     */
    fun string(clazz: Class<out ASTLeaf>? = null): Parser {
        elements.add(StringSymbol(clazz))
        return this
    }

    /**
     * [pat]に合致する識別子を追加する
     *
     * @param pat
     * @return
     */
    fun token(vararg pat: String): Parser {
        elements.add(Token(*pat))
        return this
    }

    /**
     * 抽象構文木に含めない終端記号を規則に追加する
     *
     * @param pat
     * @return
     */
    fun sep(vararg pat: String): Parser {
        elements.add(Skip(*pat))
        return this
    }

    /**
     * 非終端記号pを規則に追加する
     *
     * @param parser
     * @return
     */
    fun ast(parser: Parser): Parser {
        elements.add(Tree(parser))
        return this
    }

    /**
     * 非終端記号p1,p2,...,pnのorを規則に追加する
     *
     * @param parsers
     * @return
     */
    fun or(vararg parsers: Parser): Parser {
        elements.add(OrTree(*parsers))
        return this
    }

    /**
     * 省略可能な非終端記号を規則に追加する
     *
     * @param parser
     * @return
     */
    fun maybe(parser: Parser): Parser {
        val parser2 = Parser(parser).resetElements()
        elements.add(OrTree(parser, parser2))
        return this
    }

    /**
     * 省略可能な非終端記号を規則に追加する
     * 省略時には根だけの抽象構文木になる
     *
     * @param parser
     * @return
     */
    fun option(parser: Parser): Parser {
        elements.add(Option(parser))
        return this
    }

    /**
     * 非終端記号pの0回以上の繰り返しを規則に追加する
     *
     * @param parser
     * @return
     */
    fun repeat(parser: Parser): Parser {
        elements.add(Repeat(parser))
        return this
    }

    /**
     * 2項演算子の規則を追加する
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
