package omuretu.model

import java.lang.reflect.Method
import omuretu.ast.statement.BlockStatement
import omuretu.ast.statement.ParametersStatement
import omuretu.environment.base.VariableEnvironment

sealed class Function {
    data class OmuretuFunction(
        val parameters: ParametersStatement,
        val blockStatement: BlockStatement,
        val variableEnvironment: VariableEnvironment,
        val numberOfIdName: Int
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
