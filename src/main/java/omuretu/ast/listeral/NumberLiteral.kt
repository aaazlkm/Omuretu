package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.NumberToken
import lexer.token.Token
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.BConstOpecode
import omuretu.vertualmachine.opecode.IConstOpecode
import parser.ast.ASTTree

class NumberLiteral(
        override val token: NumberToken
) : ASTLeaf(token) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return if (argument is NumberToken) {
                NumberLiteral(argument)
            } else {
                null
            }
        }
    }

    val value: Int
        get() = token.value

    override fun toString(): String = "token: $token"

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        return Type.Defined.Int
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        val byteCodes = if (value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            BConstOpecode.createByteCode(value.toByte(), registerAt)
        } else {
            IConstOpecode.createByteCode(value, registerAt)
        }
        byteCodes.forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        return token.value
    }
}