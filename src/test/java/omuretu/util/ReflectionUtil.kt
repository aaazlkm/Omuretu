package omuretu.util

object ReflectionUtil {
    @Throws(Exception::class)
    fun <T> pickValue(obj: Any, name: String): T {
        val field = obj.javaClass.getDeclaredField(name)
        field.isAccessible = true
        return field.get(obj) as T
    }
}
