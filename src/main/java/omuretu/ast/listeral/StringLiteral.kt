package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.StringToken
import lexer.token.Token
import omuretu.environment.Environment
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.SConstOpecode
import parser.ast.ASTTree

class StringLiteral(
        override val token: StringToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTTree? {
            return if (argument is StringToken) {
                StringLiteral(argument)
            } else {
                null
            }
        }
    }

    val string: String
        get() = token.string

    override fun compile(byteCodeStore: ByteCodeStore) {
        val index = byteCodeStore.strings.size - 1
        byteCodeStore.strings[index] = string
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        SConstOpecode.createByteCode(index.toShort(), registerAt).forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    override fun evaluate(environment: Environment): Any {
        return token.string
    }
}