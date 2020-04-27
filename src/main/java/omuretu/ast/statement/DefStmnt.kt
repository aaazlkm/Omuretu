package omuretu.ast.statement

import omuretu.environment.base.VariableEnvironment
import omuretu.environment.base.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.ast.TypeTag
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.NestedVariableEnvironment
import omuretu.environment.TypeEnvironmentImpl
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function
import omuretu.typechecker.Type
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
        val parameters: ParametersStmnt,
        val typeTag: TypeTag,
        val blockStmnt: BlockStmnt
) : ASTList(listOf(idNameLiteral, parameters, blockStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_DEF = "def"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 4) return null
            val name = argument[0] as? IdNameLiteral ?: return null
            val parameters = argument[1] as? ParametersStmnt ?: return null
            val typeTag = argument[2] as? TypeTag ?: return null
            val blockStmnt = argument[3] as? BlockStmnt ?: return null
            return DefStmnt(name, parameters, typeTag, blockStmnt)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    var idNamesInDefSize: Int? = null

    override fun toString() = "($KEYWORD_DEF $idNameLiteral $parameters $blockStmnt)"

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val location = idNameLocationMap.putAndReturnLocation(name)
        environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)

        val nestIdNameLocationMap = NestedIdNameLocationMap(idNameLocationMap)
        parameters.lookupIdNamesLocation(nestIdNameLocationMap)
        blockStmnt.lookupIdNamesLocation(nestIdNameLocationMap)
        idNamesInDefSize = nestIdNameLocationMap.idNamesSize
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val environmentKey = environmentKey ?: throw OmuretuException("donot defined $this")
        val returnType = typeTag.type as? Type.Defined ?: throw OmuretuException("undefined type $this")
        val parameterTypes = parameters.types
        if (parameters.parameterNames.size != parameterTypes.size) throw OmuretuException("failed to convert parameter type :from ${parameters.parameterNames} to $parameterTypes")
        val functionType = Type.Defined.Function(returnType, parameterTypes)
        typeEnvironment.put(environmentKey, functionType)

        val bodyTypeEnvironment = TypeEnvironmentImpl(typeEnvironment)
        parameters.checkType(bodyTypeEnvironment)
        blockStmnt.checkType(bodyTypeEnvironment)
        return functionType
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

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        val nestedEnvironment = variableEnvironment as? NestedVariableEnvironment
                ?: throw OmuretuException("function can only be defined in global scode")
        val environmentKey = environmentKey ?: throw OmuretuException("donot defined def name ${idNameLiteral.name}")
        val idNamesSize = idNamesInDefSize ?: throw OmuretuException("cannnot get idNamesSize ${idNameLiteral.name}")
        val byteCodeStore = nestedEnvironment.byteCodeStore ?: throw OmuretuException("cannnot find bytecode")
        val entry = byteCodeStore.codePosition
        compile(byteCodeStore)
        variableEnvironment.put(environmentKey, Function.OmuretuFunction(parameters, blockStmnt, variableEnvironment, idNamesSize, entry))
        return idNameLiteral.token.id
    }
}