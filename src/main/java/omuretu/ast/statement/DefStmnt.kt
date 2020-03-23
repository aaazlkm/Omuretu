package omuretu.ast.statement

import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.ast.listeral.IdNameLiteral
import omuretu.exception.OmuretuException
import omuretu.model.Function
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

    var idNamesSize: Int? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val location = idNameLocationMap.putAndReturnLocation(name)
        environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)

        val nestIdNameLocationMap = NestedIdNameLocationMap(idNameLocationMap)
        parameters.lookupIdNamesLocation(nestIdNameLocationMap)
        blockStmnt.lookupIdNamesLocation(nestIdNameLocationMap)
        idNamesSize = nestIdNameLocationMap.idNamesSize
    }

    override fun evaluate(environment: Environment): Any {
        val environmentKey = environmentKey ?: throw OmuretuException("donot defined def name ${idNameLiteral.name}")
        val idNamesSize = idNamesSize ?: throw OmuretuException("cannnot get idNamesSize ${idNameLiteral.name}")
        environment.put(environmentKey, Function.OmuretuFunction(parameters, blockStmnt, environment, idNamesSize))
        return idNameLiteral.token.id
    }

    override fun toString(): String {
        return "(def $idNameLiteral $parameters $blockStmnt)"
    }
}