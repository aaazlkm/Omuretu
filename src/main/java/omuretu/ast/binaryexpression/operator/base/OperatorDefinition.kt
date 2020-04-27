package omuretu.ast.binaryexpression.operator.base

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.binaryexpression.operator.*
import parser.ast.ASTTree
import parser.element.Expression

enum class OperatorDefinition {
    ASSIGNMENT,
    EQUAL,
    LESS,
    MINUS,
    MORE,
    MULTI,
    PLUS,
    QUOTIENT,
    SURPLUS;

    companion object {
        fun from(operator: String): OperatorDefinition? {
            return values().firstOrNull { it.rawOperator == operator }
        }
    }

    val rawOperator: String
        get() {
            return when (this) {
                ASSIGNMENT -> "="
                EQUAL -> "=="
                LESS -> "<"
                MINUS -> "-"
                MORE -> ">"
                MULTI -> "*"
                PLUS -> "+"
                QUOTIENT -> "/"
                SURPLUS -> "%"
            }
        }

    val precedence: Int
        get() {
            return when (this) {
                ASSIGNMENT -> 1
                EQUAL -> 2
                LESS -> 2
                MINUS -> 3
                MORE -> 2
                MULTI -> 4
                PLUS -> 3
                QUOTIENT -> 4
                SURPLUS -> 4
            }
        }

    val assoc: Expression.Precedence.Assoc
        get() {
            return when (this) {
                ASSIGNMENT -> Expression.Precedence.Assoc.RIGHT
                EQUAL -> Expression.Precedence.Assoc.LEFT
                LESS -> Expression.Precedence.Assoc.LEFT
                MINUS -> Expression.Precedence.Assoc.LEFT
                MORE -> Expression.Precedence.Assoc.LEFT
                MULTI -> Expression.Precedence.Assoc.LEFT
                PLUS -> Expression.Precedence.Assoc.LEFT
                QUOTIENT -> Expression.Precedence.Assoc.LEFT
                SURPLUS -> Expression.Precedence.Assoc.LEFT
            }
        }

    fun createOperator(leftTree: ASTTree, rightTree: ASTTree, variableEnvironment: VariableEnvironment): Operator {
        return when (this) {
            ASSIGNMENT -> AssignmentOperator(leftTree, rightTree, variableEnvironment)
            EQUAL -> EqualOperator(leftTree, rightTree, variableEnvironment)
            LESS -> LessOperator(leftTree, rightTree, variableEnvironment)
            MINUS -> MinusOperator(leftTree, rightTree, variableEnvironment)
            MORE -> MoreOperator(leftTree, rightTree, variableEnvironment)
            MULTI -> MultiOperator(leftTree, rightTree, variableEnvironment)
            PLUS -> PlusOperator(leftTree, rightTree, variableEnvironment)
            QUOTIENT -> QuotientOperator(leftTree, rightTree, variableEnvironment)
            SURPLUS -> SurplusOperator(leftTree, rightTree, variableEnvironment)
        }
    }
}