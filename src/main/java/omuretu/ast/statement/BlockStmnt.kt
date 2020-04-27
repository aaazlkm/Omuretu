package omuretu.ast.statement

import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.BConstOpecode
import parser.ast.ASTList
import parser.ast.ASTTree

class BlockStmnt(
        private val astTrees: List<ASTTree>
) : ASTList(astTrees) {
    companion object Factory : FactoryMethod  {
        val BLOCK_START = "{"
        val BLOCK_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return BlockStmnt(argument)
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        return astTrees.map { it.checkType(typeEnvironment) }.lastOrNull() ?: Type.Defined.Int
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        if (astTrees.isEmpty()) {
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
            BConstOpecode.createByteCode(0, registerAt).forEach { byteCodeStore.addByteCode(it) }
        } else {
            val initRegisterPosition = byteCodeStore.registerPosition
            astTrees.forEach {
                // blockの最後の値しか返り値として必要ないので、初期化している
                byteCodeStore.registerPosition = initRegisterPosition
                it.compile(byteCodeStore)
            }
        }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        var result: Any? = null
        astTrees.forEach {
            result = it.evaluate(variableEnvironment)
        }
        return result ?: OMURETU_DEFAULT_RETURN_VALUE // FIXME うまくない
    }
}
