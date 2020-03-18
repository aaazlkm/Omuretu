package omuretu.ast.binaryexpression

import lexer.token.IdToken
import omuretu.exception.OmuretuException
import omuretu.ast.binaryexpression.operator.base.OperatorDefinition
import omuretu.Environment
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
        val operator = OperatorDefinition.from(operatorToken.id)?.createOperator(left, right, environment) ?: throw OmuretuException("cannnot evaluate:", this)
        return operator.calculate()
    }
}