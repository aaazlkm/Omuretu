package omuretu.environment.base

interface VariableEnvironment : Environment {
    fun put(key: EnvironmentKey, value: Any)

    fun get(key: EnvironmentKey): Any?
}
