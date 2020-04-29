package omuretu

import lexer.Lexer
import lexer.token.IdToken
import omuretu.ast.*
import omuretu.ast.expression.NegativeExpression
import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.ArrayLiteral
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import omuretu.ast.postfix.ArgumentPostfix
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.ast.statement.*
import parser.Parser
import parser.ast.ASTTree
import parser.element.Expression
import java.util.HashSet

class OmuretuParser {
    private var program = Parser.rule()

    // classの定義
    private var klass = Parser.rule(ClassStatement::class.java)
    private var classBody = Parser.rule(ClassBodyStatement::class.java)
    private var member = Parser.rule()

    // def の定義
    private var def = Parser.rule(DefStatement::class.java)
    private var paramList = Parser.rule()
    private var params = Parser.rule(ParametersStatement::class.java)
    private var param = Parser.rule(ParameterStatement::class.java)

    // block の定義
    private var block = Parser.rule(BlockStatement::class.java)

    // statement の定義
    private var statement = Parser.rule()

    // variable の定義
    private var variable = Parser.rule(VarStatement::class.java)
    private var typeTag = Parser.rule(TypeTag::class.java)

    // array の定義
    private var array = Parser.rule()

    // expression の定義
    private var expression = Parser.rule()
    private var factor = Parser.rule()
    private var primary = Parser.rule(PrimaryExpression::class.java)

    // postfix の定義
    private var postfix = Parser.rule()
    private var args = Parser.rule(ArgumentPostfix::class.java)
    private var dot = Parser.rule(DotPostfix::class.java)

    private var reserved = HashSet<String>()
    private var operators = Expression.Operators()

    init {
        program.or(
                klass,
                def,
                statement,
                Parser.rule(NullStatement::class.java)
        ).sep(";", IdToken.EOL)

        // classの定義
        klass.sep(ClassStatement.KEYWORD_CLASS)
                .identifier(reserved, IdNameLiteral::class.java)
                .option(Parser.rule().sep(ClassStatement.KEYWORD_EXTENDS).identifier(reserved, IdNameLiteral::class.java))
                .ast(classBody)
        classBody.sep(ClassBodyStatement.KEYWORD_BRACES_START)
                .option(member)
                .repeat(Parser.rule().sep(";", IdToken.EOL).option(member))
                .sep(ClassBodyStatement.KEYWORD_BRACES_END)
        member.or(
                def,
                variable
        )

        // def の定義
        def.sep(DefStatement.KEYWORD_DEF).identifier(reserved, IdNameLiteral::class.java).ast(paramList).maybe(typeTag).ast(block)
        paramList.sep(ParametersStatement.KEYWORD_PARENTHESIS_START).maybe(params).sep(ParametersStatement.KEYWORD_PARENTHESIS_END)
        params.ast(param).repeat(Parser.rule().sep(ParametersStatement.KEYWORD_PARAMETER_BREAK).ast(param))
        param.identifier(reserved, IdNameLiteral::class.java).ast(typeTag)

        // blockの定義
        block.sep(BlockStatement.BLOCK_START)
                .option(statement)
                .repeat(Parser.rule().sep(";", IdToken.EOL).option(statement))
                .sep(BlockStatement.BLOCK_END)

        // statement の定義
        statement.or(
                variable,
                Parser.rule(IfStatement::class.java).sep(IfStatement.KEYWORD_IF).ast(expression).ast(block).option(Parser.rule().sep(IfStatement.KEYWORD_ELSE).ast(block)),
                Parser.rule(WhileStatement::class.java).sep(WhileStatement.KEYWORD_WHILE).ast(expression).ast(block)
        )

        // variable の定義
        variable.sep(VarStatement.KEYWORD_VAR).identifier(reserved, IdNameLiteral::class.java).maybe(typeTag).sep(VarStatement.KEYWORD_EQUAL).ast(expression)
        typeTag.sep(TypeTag.KEYWORD_COLON).identifier(reserved, IdNameLiteral::class.java)

        // arrayの定義
        array.sep(ArrayLiteral.KEYWORD_BRACKETS_START)
                .maybe(Parser.rule(ArrayLiteral::class.java).ast(expression).repeat(Parser.rule().sep(ArrayLiteral.KEYWORD_PARAMETER_BREAK).ast(expression)))
                .sep(ArrayLiteral.KEYWORD_BRACKETS_END)

        // expression の定義
        expression.expression(factor, operators, BinaryExpression::class.java)
        factor.or(
                Parser.rule(NegativeExpression::class.java).sep("-").ast(primary),
                primary
        )
        primary.or(
                array,
                Parser.rule().sep("(").ast(expression).sep(")"),
                Parser.rule().number(NumberLiteral::class.java),
                Parser.rule().identifier(reserved, IdNameLiteral::class.java),
                Parser.rule().string(StringLiteral::class.java)
        ).repeat(postfix)

        // postfix の定義
        postfix.or(
                dot,
                Parser.rule().sep(ArgumentPostfix.KEYWORD_PARENTHESIS_START).maybe(args).sep(ArgumentPostfix.KEYWORD_PARENTHESIS_END),
                Parser.rule(ArrayPostfix::class.java).sep(ArrayPostfix.KEYWORD_BRACKETS_START).ast(expression).sep(ArrayPostfix.KEYWORD_BRACKETS_END)
        )
        args.ast(expression).repeat(Parser.rule().sep(ArgumentPostfix.KEYWORD_ARGUMENT_BREAK).ast(expression))
        dot.sep(DotPostfix.KEYWORD_DOT).identifier(reserved, IdNameLiteral::class.java)

        reserved.add(":")
        reserved.add(";")
        reserved.add("}")
        reserved.add(")")
        reserved.add("]")
        reserved.add(IdToken.EOL)

        OperatorDefinition.values().forEach {
            operators.add(it.rawOperator, it.precedence, it.assoc)
        }
    }

    fun parse(lexer: Lexer): ASTTree {
        return program.parseTokens(lexer)
    }
}