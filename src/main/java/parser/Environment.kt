package parser

interface Environment {
    fun put(key: String, value: Any)
    fun get(key: String): Any?
}