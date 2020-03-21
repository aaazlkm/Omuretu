package omuretu.ast.statement

import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap
import omuretu.exception.OmuretuException
import omuretu.model.Function
import parser.ast.ASTList
import parser.ast.ASTTree

class ClosureStmnt(
        val parameters: ParameterStmnt,
        val blockStmnt: BlockStmnt
) : ASTList(listOf(parameters, blockStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLOSURE = "closure"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val parameters = argument[0] as? ParameterStmnt ?: return null
            val blockStmnt = argument[1] as? BlockStmnt ?: return null
            return ClosureStmnt(parameters, blockStmnt)
        }
    }

    private var numberOfIdName: Int? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val nestedIdNameLocationMap = NestedIdNameLocationMap(idNameLocationMap)
        parameters.lookupIdNamesLocation(nestedIdNameLocationMap)
        blockStmnt.lookupIdNamesLocation(nestedIdNameLocationMap)
        numberOfIdName = nestedIdNameLocationMap.idNamesSize
    }

    override fun evaluate(environment: Environment): Any {
        return numberOfIdName?.let {
            Function.OmuretuFunction(parameters, blockStmnt, environment, it)
        } ?: throw OmuretuException("cannot get idNames size")
    }

    override fun toString(): String {
        return "(closure $parameters $blockStmnt)"
    }
}