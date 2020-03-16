package omuretu

interface Environment {
    fun put(key: String, value: Any)
    fun get(key: String): Any?
}

class BasicEnvironment: Environment {
    private val keyToValue = mutableMapOf<String, Any>()

    override fun put(key: String, value: Any) {
        this.keyToValue[key] = value
    }

    override fun get(key: String): Any? {
        return this.keyToValue[key]
    }
}

class NestedEnvironment(
        private val outEnvironment: NestedEnvironment? = null
): Environment {
    private val keyToValue = mutableMapOf<String, Any>()

    //region Environment

    override fun get(key: String): Any? {
        return keyToValue[key] ?: outEnvironment?.get(key)
    }

    override fun put(key: String, value: Any) {
        val environment = searchEnvironmentHasThisKey(key) ?: this
        environment.putInThisEnvironment(key, value)
    }

    //endregion

    fun putInThisEnvironment(key: String, value: Any) {
        keyToValue[key] = value
    }

    fun searchEnvironmentHasThisKey(key: String): NestedEnvironment? {
        return if (keyToValue[key] != null)
            this
        else {
            outEnvironment?.searchEnvironmentHasThisKey(key)
        }
    }
}