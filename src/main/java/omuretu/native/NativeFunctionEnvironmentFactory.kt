package omuretu.native

import omuretu.NestedEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function
import java.lang.reflect.Method
import javax.swing.JOptionPane

object NativeFunctionEnvironmentFactory {
    private data class FunctionDefinition(
            val name: String,
            val parameterType: Array<Class<*>> = arrayOf()
    )

    private val functionDefinitions = listOf<FunctionDefinition>(
            FunctionDefinition("print", arrayOf(Any::class.java)),
            FunctionDefinition("read"),
            FunctionDefinition("length", arrayOf(String::class.java)),
            FunctionDefinition("toInt", arrayOf(Any::class.java)),
            FunctionDefinition("getCurrentTimeMillis")
    )

    fun create(): NestedEnvironment {
        val environment = NestedEnvironment()
        functionDefinitions
                .map { it.name to getMethodByReflection(it.name, *it.parameterType) }
                .forEach { (name, nativeMethod) ->
                    environment.put(name, Function.NativeFunction(name, nativeMethod, nativeMethod.parameterCount))
                }
        return environment
    }

    private fun getMethodByReflection(functionName: String, vararg parameterType: Class<*>): Method {
        return try {
            NativeFunctionEnvironmentFactory::class.java.getMethod(functionName, *parameterType)
        } catch (e: Exception) {
            throw OmuretuException("cannot find a native function: $functionName")
        }
    }

    //region native methods

    @JvmStatic
    fun print(value: Any): Int {
        println(value.toString())
        return 0
    }

    @JvmStatic
    fun read(): String {
        return JOptionPane.showInputDialog(null)
    }

    @JvmStatic
    fun length(string: String): Int {
        return string.length
    }

    @JvmStatic
    fun toInt(value: Any): Int {
        return if (value is String) {
            Integer.parseInt(value)
        } else {
            (value as? Int)?.toInt()
        } ?: throw NumberFormatException(value.toString())
    }

    @JvmStatic
    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    //endregion
}