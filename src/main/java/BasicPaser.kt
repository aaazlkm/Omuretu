import ast.*
import lexer.Lexer
import lexer.token.IdToken
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class BasicParser {
    private var reserved = HashSet<String>()
    private var operators = Expression.Operators()
    private var expression0 = Parser.rule()
    private var primary = Parser.rule(PrimaryExpression::class.java)
            .or(
                    Parser.rule().sep("(").ast(expression0).sep(")"),
                    Parser.rule().number(NumberLiteral::class.java),
                    Parser.rule().identifier(reserved, Name::class.java),
                    Parser.rule().string(StringLiteral::class.java)
            )
    private var factor = Parser.rule()
            .or(
                    Parser.rule(NegativeExpression::class.java).sep("-").ast(primary),
                    primary
            )
    private var expression = expression0
            .expression(factor, operators, BinaryExpression::class.java)

    private var statement0 = Parser.rule()
    private var block = Parser.rule(BlockStmnt::class.java)
            .sep(BlockStmnt.BLOCK_START)
            .option(statement0)
            .repeat(Parser.rule().sep(";", IdToken.EOL).option(statement0))
            .sep(BlockStmnt.BLOCK_END)
    private var simple = Parser.rule(PrimaryExpression::class.java).ast(expression)
    private var statement = statement0
            .or(
                    Parser.rule(IfStmnt::class.java).sep(IfStmnt.KEYWORD_IF).ast(expression).ast(block).option(Parser.rule().sep(IfStmnt.KEYWORD_ELSE).ast(block)),
                    Parser.rule(WhileStmnt::class.java).sep(WhileStmnt.KEYWORD_WHILE).ast(expression).ast(block),
                    simple
            )
    private var program = Parser.rule()
            .or(
                    statement,
                    Parser.rule(NullStmnt::class.java)
            )
            .sep(";", IdToken.EOL)

    init {
        reserved.add(";")
        reserved.add("}")
        reserved.add(IdToken.EOL)

        operators.add("=", 1, Expression.Precedence.Assoc.RIGHT)
        operators.add("==", 2, Expression.Precedence.Assoc.LEFT)
        operators.add(">", 2, Expression.Precedence.Assoc.LEFT)
        operators.add("<", 2, Expression.Precedence.Assoc.LEFT)
        operators.add("+", 3, Expression.Precedence.Assoc.LEFT)
        operators.add("-", 3, Expression.Precedence.Assoc.LEFT)
        operators.add("*", 4, Expression.Precedence.Assoc.LEFT)
        operators.add("/", 4, Expression.Precedence.Assoc.LEFT)
        operators.add("%", 4, Expression.Precedence.Assoc.LEFT)
    }

    fun parse(lexer: Lexer): ASTTree {
        return program.parseTokens(lexer)
    }
}