package omuretu.native

import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function
import omuretu.typechecker.Type
import java.lang.reflect.Method
import javax.swing.JOptionPane

object NativeFunctionEnvironmentFactory {
    private data class FunctionDefinition(
            val name: String,
            val functionType: Type.Defined.Function,
            val parameterType: Array<Class<*>> = arrayOf()
    )

    private val functionDefinitions = listOf(
            FunctionDefinition("print", Type.Defined.Function(Type.Defined.Int, listOf(Type.Defined.Any)), arrayOf(Any::class.java)),
            FunctionDefinition("read", Type.Defined.Function(Type.Defined.String)),
            FunctionDefinition("length", Type.Defined.Function(Type.Defined.Int, listOf(Type.Defined.String)), arrayOf(String::class.java)),
            FunctionDefinition("toInt", Type.Defined.Function(Type.Defined.Int, listOf(Type.Defined.Any)), arrayOf(Any::class.java)),
            FunctionDefinition("getCurrentTimeMillis", Type.Defined.Function(Type.Defined.Int))
    )

    fun createBasedOn(variableEnvironment: GlobalVariableEnvironment, typeEnvironment: TypeEnvironment): GlobalVariableEnvironment {
        functionDefinitions
                .forEach { functionDefinition ->
                    val nativeMethod = getMethodByReflection(functionDefinition.name, *functionDefinition.parameterType)

                    variableEnvironment.putValueByIdName(functionDefinition.name, Function.NativeFunction(functionDefinition.name, nativeMethod, nativeMethod.parameterCount))
                    variableEnvironment.idNameLocationMap.getLocationFromAllMap(functionDefinition.name)?.let {
                        typeEnvironment.put(EnvironmentKey(it.ancestorAt, it.indexInIdNames), functionDefinition.functionType)
                    }
                }
        return variableEnvironment
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