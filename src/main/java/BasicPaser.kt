import ast.*
import lexer.Lexer
import lexer.token.IdToken
import lexer.token.Token
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class BasicParser {
    internal var reserved = HashSet<String>()
    internal var operators = Expression.Operators()
    internal var expr0 = Parser.rule()
    internal var primary = Parser.rule(PrimaryExpression::class.java)
            .or(Parser.rule().sep("(").ast(expr0).sep(")"),
                    Parser.rule().number(NumberLiteral::class.java),
                    Parser.rule().identifier(reserved, Name::class.java),
                    Parser.rule().string(StringLiteral::class.java))
    internal var factor = Parser.rule().or(Parser.rule(NegativeExpression::class.java).sep("-").ast(primary), primary)
    internal var expr = expr0.expression(factor, operators, BinaryExpression::class.java)

    internal var statement0 = Parser.rule()
    internal var block = Parser.rule(BlockStmnt::class.java)
            .sep("{").option(statement0)
            .repeat(Parser.rule().sep(";", IdToken.EOL).option(statement0))
            .sep("}")
    internal var simple = Parser.rule(PrimaryExpression::class.java).ast(expr)
    internal var statement = statement0.or(
            Parser.rule(IfStmnt::class.java).sep("if").ast(expr).ast(block).option(Parser.rule().sep("else").ast(block)),
            Parser.rule(WhileStmnt::class.java).sep("while").ast(expr).ast(block),
            simple
    )

    internal var program = Parser.rule().or(statement, Parser.rule(NullStmnt::class.java))
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