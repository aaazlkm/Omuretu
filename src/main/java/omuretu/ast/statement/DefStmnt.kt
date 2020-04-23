package omuretu.ast.statement

import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.NestedEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.MoveOpecode
import omuretu.vertualmachine.opecode.RestoreOpecode
import omuretu.vertualmachine.opecode.ReturnOpecode
import omuretu.vertualmachine.opecode.SaveOpecode
import parser.ast.ASTList
import parser.ast.ASTTree

class DefStmnt(
        val idNameLiteral: IdNameLiteral,
        val parameters: ParameterStmnt,
        val blockStmnt: BlockStmnt
) : ASTList(listOf(idNameLiteral, parameters, blockStmnt)) {
    companion object Factory : FactoryMethod  {
        const val KEYWORD_DEF = "def"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val name = argument[0] as? IdNameLiteral ?: return null
            val parameters = argument[1] as? ParameterStmnt ?: return null
            val blockStmnt = argument[2] as? BlockStmnt ?: return null
            return DefStmnt(name, parameters, blockStmnt)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    var idNamesInDefSize: Int? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val location = idNameLocationMap.putAndReturnLocation(name)
        environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)

        val nestIdNameLocationMap = NestedIdNameLocationMap(idNameLocationMap)
        parameters.lookupIdNamesLocation(nestIdNameLocationMap)
        blockStmnt.lookupIdNamesLocation(nestIdNameLocationMap)
        idNamesInDefSize = nestIdNameLocationMap.idNamesSize
    }

    override fun compile(byteCodeStore: ByteCodeStore) {
        val idNamesInDefSize = idNamesInDefSize ?: throw OmuretuException("failed to search idnames size in def $this")

        byteCodeStore.apply {
            setRegisterAt(0)
            stackFrameSize = idNamesInDefSize + OmuretuVirtualMachine.SAVE_AREA_SIZE

            SaveOpecode.createByteCode(idNamesInDefSize.toByte()).forEach { byteCodeStore.addByteCode(it) }

            blockStmnt.compile(byteCodeStore)

            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
            MoveOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) }

            RestoreOpecode.createByteCode(idNamesInDefSize.toByte()).forEach { byteCodeStore.addByteCode(it) }

            ReturnOpecode.createByteCode().forEach { byteCodeStore.addByteCode(it) }
        }
    }

    override fun evaluate(environment: Environment): Any {
        val nestedEnvironment = environment as? NestedEnvironment ?: throw OmuretuException("function can only be defined in global scode")
        val environmentKey = environmentKey ?: throw OmuretuException("donot defined def name ${idNameLiteral.name}")
        val idNamesSize = idNamesInDefSize ?: throw OmuretuException("cannnot get idNamesSize ${idNameLiteral.name}")
        val byteCodeStore = nestedEnvironment.byteCodeStore ?: throw OmuretuException("cannnot find bytecode")
        val entry = byteCodeStore.codePosition
        compile(byteCodeStore)
        environment.put(environmentKey, Function.OmuretuFunction(parameters, blockStmnt, environment, idNamesSize, entry))
        return idNameLiteral.token.id
    }

    override fun toString(): String {
        return "(def $idNameLiteral $parameters $blockStmnt)"
    }
}