package omuretu.ast.binaryexpression

import lexer.token.IdToken
import omuretu.exception.OmuretuException
import omuretu.ast.binaryexpression.operator.base.OperatorDefinition
import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap
import omuretu.ast.binaryexpression.operator.base.LeftValueOperator
import omuretu.ast.binaryexpression.operator.base.RightValueOperator
import omuretu.ast.listeral.IdNameLiteral
import omuretu.model.InlineCache
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.base.ComputeOpecode
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

    private var inlineCache: InlineCache? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", this)
        when {
            operatorToken.id == OperatorDefinition.ASSIGNMENT.rawOperator && left is IdNameLiteral -> { // FIXME うまくない気がする
                left.lookupIdNamesForAssign(idNameLocationMap)
                right.lookupIdNamesLocation(idNameLocationMap)
            }
            else -> {
                left.lookupIdNamesLocation(idNameLocationMap)
                right.lookupIdNamesLocation(idNameLocationMap)
            }
        }
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot compile:", this)
        if (operatorToken.id == OperatorDefinition.ASSIGNMENT.rawOperator) {
            val leftIdName = left as? IdNameLiteral ?: throw OmuretuException("cannnot compile:", this)
            right.compile(byteCodeStore)
            leftIdName.compileAssign(byteCodeStore)
        } else {
            left.compile(byteCodeStore)
            right.compile(byteCodeStore)
            val registerLeftAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 2)
            val registerRightAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
            ComputeOpecode.createByteCode(operatorToken.id, registerLeftAt, registerRightAt).forEach {
                byteCodeStore.addByteCode(it)
            }
            byteCodeStore.prevRegister()
        }
    }

    override fun evaluate(environment: Environment): Any {
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", this)
        val operator = OperatorDefinition.from(operatorToken.id)?.createOperator(left, right, environment)
                ?: throw OmuretuException("cannnot evaluate:", this)
        return when (operator) {
            is LeftValueOperator -> {
                operator.calculate(inlineCache) {
                    this.inlineCache = it
                }
            }
            is RightValueOperator -> {
                operator.calculate()
            }
            else -> {
                throw OmuretuException("undefined operator: $operator ", this)
            }
        }
    }
}