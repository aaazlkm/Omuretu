package omuretu.parser

import omuretu.ast.*
import lexer.Lexer
import lexer.token.IdToken
import omuretu.ast.binaryexpression.BinaryExpression
import omuretu.ast.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.NameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class BasicParser {
    private var reserved = HashSet<String>()
    private var operators = Expression.Operators()

    private var primary = Parser.rule(PrimaryExpression::class.java)
    private var factor = Parser.rule()
    private var expression = Parser.rule()

    private var block = Parser.rule(BlockStmnt::class.java)
    private var simple = Parser.rule(PrimaryExpression::class.java)
    private var statement = Parser.rule()

    private var program = Parser.rule()

    init {
        primary.or(
                Parser.rule().sep("(").ast(expression).sep(")"),
                Parser.rule().number(NumberLiteral::class.java),
                Parser.rule().identifier(reserved, NameLiteral::class.java),
                Parser.rule().string(StringLiteral::class.java)
        )
        factor.or(
                Parser.rule(NegativeExpression::class.java).sep("-").ast(primary),
                primary
        )
        expression.expression(factor, operators, BinaryExpression::class.java)

        block.sep(BlockStmnt.BLOCK_START)
                .option(statement)
                .repeat(Parser.rule().sep(";", IdToken.EOL).option(statement))
                .sep(BlockStmnt.BLOCK_END)
        simple.ast(expression)
        statement.or(
                Parser.rule(IfStmnt::class.java).sep(IfStmnt.KEYWORD_IF).ast(expression).ast(block).option(Parser.rule().sep(IfStmnt.KEYWORD_ELSE).ast(block)),
                Parser.rule(WhileStmnt::class.java).sep(WhileStmnt.KEYWORD_WHILE).ast(expression).ast(block),
                simple
        )

        program.or(
                statement,
                Parser.rule(NullStmnt::class.java)
        ).sep(";", IdToken.EOL)

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