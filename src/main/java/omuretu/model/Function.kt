package omuretu.model

import omuretu.Environment
import omuretu.ast.BlockStmnt
import omuretu.ast.NameLiteral
import omuretu.ast.ParameterList

data class Function(
        val parameters: ParameterList,
        val blockStmnt: BlockStmnt,
        val environment: Environment
) {
    override fun toString(): String {
        return  "<fun: ${hashCode()} >";
    }
}