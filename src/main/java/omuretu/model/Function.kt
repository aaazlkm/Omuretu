package omuretu.model

import omuretu.Environment
import omuretu.ast.statement.BlockStmnt
import omuretu.ast.statement.ParameterStmnt
import java.lang.reflect.Method

sealed class Function {
    data class OmuretuFunction(
            val parameters: ParameterStmnt,
            val blockStmnt: BlockStmnt,
            val environment: Environment
    ): Function()

    data class NativeFunction(
            val name: String,
            val method: Method,
            val numberOfParameter: Int
    ): Function()

    override fun toString(): String {
         return when(this) {
            is OmuretuFunction ->  "<fun: ${hashCode()} >"
            is NativeFunction ->  "<native fun: ${hashCode()} >"
        }
    }
}