package omuretu.ast.expression.binaryexpression.operator.base

import omuretu.ast.expression.binaryexpression.operator.AssignmentOperator
import omuretu.ast.expression.binaryexpression.operator.EqualOperator
import omuretu.ast.expression.binaryexpression.operator.LessOperator
import omuretu.ast.expression.binaryexpression.operator.MinusOperator
import omuretu.ast.expression.binaryexpression.operator.MoreOperator
import omuretu.ast.expression.binaryexpression.operator.MultiOperator
import omuretu.ast.expression.binaryexpression.operator.PlusOperator
import omuretu.ast.expression.binaryexpression.operator.QuotientOperator
import omuretu.ast.expression.binaryexpression.operator.SurplusOperator
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

    fun createOperator(leftTree: ASTTree, rightTree: ASTTree): Operator {
        return when (this) {
            ASSIGNMENT -> AssignmentOperator(leftTree, rightTree)
            EQUAL -> EqualOperator(leftTree, rightTree)
            LESS -> LessOperator(leftTree, rightTree)
            MINUS -> MinusOperator(leftTree, rightTree)
            MORE -> MoreOperator(leftTree, rightTree)
            MULTI -> MultiOperator(leftTree, rightTree)
            PLUS -> PlusOperator(leftTree, rightTree)
            QUOTIENT -> QuotientOperator(leftTree, rightTree)
            SURPLUS -> SurplusOperator(leftTree, rightTree)
        }
    }
}
