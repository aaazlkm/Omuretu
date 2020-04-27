package omuretu.ast

import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.NegOpecode
import parser.ast.ASTList
import parser.ast.ASTTree

class NegativeExpression(
        val operand: ASTTree
) : ASTList(listOf(operand)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTList? {
            if (argument.size != 1) return null
            return NegativeExpression(argument[0])
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val type = operand.checkType(typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int, type, this, typeEnvironment)
        return type
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        operand.compile(byteCodeStore)
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
        NegOpecode.createByteCode(registerAt).forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        val result = operand.evaluate(variableEnvironment)
        return if (result is Int) {
            -result
        } else {
            throw OmuretuException("bad type for  -", this)
        }
    }

    override fun toString(): String {
        return "-$operand"
    }
}