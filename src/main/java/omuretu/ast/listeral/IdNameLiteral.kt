package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import omuretu.exception.OmuretuException
import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.GmoveOpecode
import omuretu.vertualmachine.opecode.MoveOpecode

class IdNameLiteral(
        override val token: IdToken
) : ASTLeaf(token) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTLeaf? {
            return if (argument is IdToken) {
                IdNameLiteral(argument)
            } else {
                null
            }
        }
    }

    val name: String
        get() = token.id

    private var environmentKey: EnvironmentKey? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        idNameLocationMap.getLocationFromAllMap(name)?.let {
            environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        } ?: run {
            throw OmuretuException("undefined name: $name")
        }
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        val environmentKey = environmentKey ?: throw OmuretuException("undefined name: $name")
        when {
            environmentKey.ancestorAt > 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
                GmoveOpecode.createByteCode(environmentKey.index.toShort(), registerAt).forEach { byteCodeStore.addByteCode(it) }
            }
            environmentKey.ancestorAt == 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
                MoveOpecode.createByteCode(environmentKey.index.toByte(), registerAt).forEach { byteCodeStore.addByteCode(it) }
            }
            else -> {
                throw OmuretuException("undefined name: $name")
            }
        }
    }

    fun compileAssign(byteCodeStore: ByteCodeStore) {
        val environmentKey = environmentKey ?: throw OmuretuException("undefined name: $name")
        when {
            environmentKey.ancestorAt > 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
                GmoveOpecode.createByteCode(registerAt, environmentKey.index.toShort()).forEach { byteCodeStore.addByteCode(it) }
            }
            environmentKey.ancestorAt == 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
                MoveOpecode.createByteCode(registerAt, environmentKey.index.toByte()).forEach { byteCodeStore.addByteCode(it) }
            }
            else -> {
                throw OmuretuException("undefined name: $name")
            }
        }
    }

    override fun evaluate(environment: Environment): Any {
        return environmentKey?.let { environment.get(it) } ?: throw OmuretuException("undefined name: ${token.id}", this)
    }

    fun lookupIdNamesForAssign(idNameLocationMap: NestedIdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(name).let {
            environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
    }

    fun evaluateForAssign(environment: Environment, value: Any) {
        environmentKey?.let { environment.put(it, value) } ?: throw OmuretuException("undefined name: ${token.id}", this)
    }
}