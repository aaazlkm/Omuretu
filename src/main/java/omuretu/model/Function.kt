package omuretu.model

import omuretu.Environment
import omuretu.ast.BlockStmnt
import omuretu.ast.ParameterList
import java.lang.reflect.Method

sealed class Function {
    data class OmuretuFunction(
            val parameters: ParameterList,
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