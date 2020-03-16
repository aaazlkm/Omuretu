package omuretu.ast

import lexer.token.IdToken
import omuretu.exception.OmuretuException
import omuretu.operator.base.LValueOperator
import omuretu.operator.base.OperatorDefinition
import omuretu.operator.base.RValueOperator
import parser.Environment
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree

class BinaryExpression(
        val left: ASTTree,
        val operator: ASTLeaf,
        val right: ASTTree
) : ASTList(listOf(left, operator, right)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val operation = argument[1] as? ASTLeaf ?: return null
            return BinaryExpression(argument[0], operation, argument[2])
        }
    }

    override fun evaluate(environment: Environment): Any {
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", this)
        val operator = OperatorDefinition.from(operatorToken.id)?.getOperator() ?: throw OmuretuException("cannnot evaluate:", this)
        return when(operator) {
            is LValueOperator -> {
                val key = left as? Name ?: throw OmuretuException("cannnot evaluate:", this)
                val rightValue = right.evaluate(environment)
                operator.calculate(key.token.id, rightValue, environment)
                rightValue
            }
            is RValueOperator -> {
                val leftValue = left.evaluate(environment)
                val rightValue = right.evaluate(environment)
                operator.calculate(leftValue, rightValue) ?: throw OmuretuException("cannnot evaluate:", this)
            }
            else -> {
                // ここの分岐は想定していない
                throw OmuretuException("this operator is not defined.", this)
            }
        }

    }
}