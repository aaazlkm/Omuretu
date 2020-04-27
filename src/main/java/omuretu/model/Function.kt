package omuretu.model

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.statement.BlockStmnt
import omuretu.ast.statement.ParametersStmnt
import java.lang.reflect.Method

sealed class Function {
    data class OmuretuFunction(
            val parameters: ParametersStmnt,
            val blockStmnt: BlockStmnt,
            val variableEnvironment: VariableEnvironment,
            val numberOfIdName: Int,
            val entry: Int
    ) : Function() {
        val numberOfParameter: Int
            get() = parameters.numberOfChildren
    }

    data class NativeFunction(
            val name: String,
            val method: Method,
            val numberOfParameter: Int
    ) : Function() {
        fun invoke(vararg arguments: Any) {
            method.invoke(null, arguments)
        }
    }

    override fun toString(): String {
        return when (this) {
            is OmuretuFunction -> "<fun: ${hashCode()} >"
            is NativeFunction -> "<native fun: ${hashCode()} >"
        }
    }
}