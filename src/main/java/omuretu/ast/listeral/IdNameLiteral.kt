package omuretu.ast.listeral

import lexer.token.IdToken
import lexer.token.Token
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.GmoveOpecode
import omuretu.vertualmachine.opecode.MoveOpecode
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import parser.ast.ASTLeaf

data class IdNameLiteral(
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

    var environmentKey: EnvironmentKey? = null

    override fun toString() = name

    override fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap) {
        idNameLocationVisitor.visit(this, idNameLocationMap)
    }

    override fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type {
        return checkTypeVisitor.visit(this, typeEnvironment)
    }

    override fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {
        compileVisitor.visit(this, byteCodeStore)
    }

    override fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any {
        return evaluateVisitor.visit(this, variableEnvironment)
    }

    fun lookupIdNamesForAssign(idNameLocationMap: IdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(name).let {
            environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
    }

    fun checkTypeForAssign(typeEnvironment: TypeEnvironment, newType: Type): Type {
        val environmentKey = environmentKey ?: throw OmuretuException("undefined name: $name")
        val type = typeEnvironment.get(environmentKey)
        if (type == null) {
            typeEnvironment.put(environmentKey, newType)
        } else {
            TypeCheckHelper.checkSubTypeOrThrow(type, newType, this, typeEnvironment)
        }
        return type ?: newType
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

    fun evaluateForAssign(variableEnvironment: VariableEnvironment, value: Any) {
        environmentKey?.let { variableEnvironment.put(it, value) } ?: throw OmuretuException("undefined name: ${token.id}", this)
    }
}