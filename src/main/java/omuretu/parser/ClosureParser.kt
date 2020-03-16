package omuretu.parser

import lexer.Lexer
import lexer.token.IdToken
import omuretu.ast.*
import omuretu.ast.binaryexpression.BinaryExpression
import omuretu.ast.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.postfix.Argument
import omuretu.ast.postfix.Postfix
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class ClosureParser {
    private var reserved = HashSet<String>()
    private var operators = Expression.Operators()

    private var program = Parser.rule()

    private var def = Parser.rule(DefStmnt::class.java)
    private var paramList = Parser.rule()
    private var params = Parser.rule(ParameterList::class.java)
    private var param = Parser.rule()

    private var statement = Parser.rule()
    private var simple = Parser.rule(PrimaryExpression::class.java)
    private var block = Parser.rule(BlockStmnt::class.java)

    private var expression = Parser.rule()
    private var factor = Parser.rule()
    private var primary = Parser.rule(PrimaryExpression::class.java)

    private var postfix = Parser.rule()
    private var args = Parser.rule(Argument::class.java)

    init {
        program.or(
                def,
                statement,
                Parser.rule(NullStmnt::class.java)
        ).sep(";", IdToken.EOL)

        // def の定義
        def.sep(DefStmnt.KEYWORD_DEF).identifier(reserved, NameLiteral::class.java).ast(paramList).ast(block)
        paramList.sep(ParameterList.KEYWORD_PARENTHESIS_START).maybe(params).sep(ParameterList.KEYWORD_PARENTHESIS_END)
        params.ast(param).repeat(Parser.rule().sep(ParameterList.KEYWORD_PARAMETER_BREAK).ast(param))
        param.identifier(reserved, NameLiteral::class.java)

        // statement の定義
        statement.or(
                Parser.rule(IfStmnt::class.java).sep(IfStmnt.KEYWORD_IF).ast(expression).ast(block).option(Parser.rule().sep(IfStmnt.KEYWORD_ELSE).ast(block)),
                Parser.rule(WhileStmnt::class.java).sep(WhileStmnt.KEYWORD_WHILE).ast(expression).ast(block),
                simple
        )
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
                Parser.rule().identifier(reserved, NameLiteral::class.java),
                Parser.rule().string(StringLiteral::class.java)
        ).repeat(postfix)

        // postfix の定義
        postfix.sep(Postfix.KEYWORD_PARENTHESIS_START).maybe(args).sep(Postfix.KEYWORD_PARENTHESIS_END)
        args.ast(expression).repeat(Parser.rule().sep(Argument.KEYWORD_ARGUMENT_BREAK).ast(expression))

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