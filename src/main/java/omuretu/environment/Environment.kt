package omuretu.environment

interface Environment {
    fun put(key: EnvironmentKey, value: Any)

    fun get(key: EnvironmentKey): Any?
}