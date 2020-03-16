package omuretu.parser.basic

import parser.Environment

class BasicEnvironment: Environment {
    private val keyToValue = mutableMapOf<String, Any>()

    override fun put(key: String, value: Any) {
        this.keyToValue[key] = value
    }

    override fun get(key: String): Any? {
        return this.keyToValue[key]
    }
}