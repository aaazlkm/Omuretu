package omuretu.parser

import lexer.Lexer
import lexer.token.IdToken
import omuretu.ast.*
import omuretu.ast.binaryexpression.BinaryExpression
import omuretu.ast.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import omuretu.ast.postfix.ArgumentPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.ast.postfix.Postfix
import omuretu.ast.statement.*
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class ClassParser {
    private var reserved = HashSet<String>()
    private var operators = Expression.Operators()

    private var program = Parser.rule()

    private var klass = Parser.rule(ClassStmnt::class.java)
    private var classBody = Parser.rule(ClassBodyStmnt::class.java)
    private var member = Parser.rule()

    private var def = Parser.rule(DefStmnt::class.java)
    private var paramList = Parser.rule()
    private var params = Parser.rule(ParameterStmnt::class.java)
    private var param = Parser.rule()

    private var statement = Parser.rule()

    private var simple = Parser.rule(PrimaryExpression::class.java)
    private var block = Parser.rule(BlockStmnt::class.java)

    private var expression = Parser.rule()
    private var factor = Parser.rule()
    private var primary = Parser.rule(PrimaryExpression::class.java)

    private var postfix = Parser.rule()
    private var args = Parser.rule(ArgumentPostfix::class.java)
    private var dot = Parser.rule(DotPostfix::class.java)

    init {
        program.or(
                klass,
                def,
                statement,
                Parser.rule(NullStmnt::class.java)
        ).sep(";", IdToken.EOL)

        // classの定義
        klass.sep(ClassStmnt.KEYWORD_CLASS)
                .identifier(reserved, IdNameLiteral::class.java)
                .option(Parser.rule().sep(ClassStmnt.KEYWORD_EXTENDS).identifier(reserved, IdNameLiteral::class.java))
                .ast(classBody)
        classBody.sep(ClassBodyStmnt.KEYWORD_BRACES_START)
                .option(member)
                .repeat(Parser.rule().sep(";", IdToken.EOL).option(member))
                .sep(ClassBodyStmnt.KEYWORD_BRACES_END)
        member.or(
                def,
                simple
        )

        // def の定義
        def.sep(DefStmnt.KEYWORD_DEF).identifier(reserved, IdNameLiteral::class.java).ast(paramList).ast(block)
        paramList.sep(ParameterStmnt.KEYWORD_PARENTHESIS_START).maybe(params).sep(ParameterStmnt.KEYWORD_PARENTHESIS_END)
        params.ast(param).repeat(Parser.rule().sep(ParameterStmnt.KEYWORD_PARAMETER_BREAK).ast(param))
        param.identifier(reserved, IdNameLiteral::class.java)

        // statement の定義
        statement.or(
                Parser.rule(IfStmnt::class.java).sep(IfStmnt.KEYWORD_IF).ast(expression).ast(block).option(Parser.rule().sep(IfStmnt.KEYWORD_ELSE).ast(block)),
                Parser.rule(WhileStmnt::class.java).sep(WhileStmnt.KEYWORD_WHILE).ast(expression).ast(block),
                simple
        )

        // simple
        simple.ast(expression).option(args)
        block.sep(BlockStmnt.BLOCK_START)
                .option(statement)
                .repeat(Parser.rule().sep(";", IdToken.EOL).option(statement))
                .sep(BlockStmnt.BLOCK_END)

        // expression の定義
        expression.expression(factor, operators, BinaryExpression::class.java)
        factor.or(
                Parser.rule(NegativeExpression::class.java).sep("-").ast(primary),
                primary
        )
        primary.or(
                Parser.rule(ClosureStmnt::class.java).sep(ClosureStmnt.KEYWORD_CLOSURE).ast(paramList).ast(block),
                Parser.rule().sep("(").ast(expression).sep(")"),
                Parser.rule().number(NumberLiteral::class.java),
                Parser.rule().identifier(reserved, IdNameLiteral::class.java),
                Parser.rule().string(StringLiteral::class.java)
        ).repeat(postfix)

        // postfix の定義
        postfix.or(
                dot,
                Parser.rule().sep(Postfix.KEYWORD_PARENTHESIS_START).maybe(args).sep(Postfix.KEYWORD_PARENTHESIS_END)
        )
        args.ast(expression).repeat(Parser.rule().sep(ArgumentPostfix.KEYWORD_ARGUMENT_BREAK).ast(expression))
        dot.sep(DotPostfix.KEYWORD_DOT).identifier(reserved, IdNameLiteral::class.java)

        reserved.add(";")
        reserved.add("}")
        reserved.add(")")
        reserved.add(IdToken.EOL)

        OperatorDefinition.values().forEach {
            operators.add(it.rawOperator, it.precedence, it.assoc)
        }
    }

    fun parse(lexer: Lexer): ASTTree {
        return program.parseTokens(lexer)
    }
}