package omuretu.vertualmachine

interface HeapMemory {
    fun read(index: Int): Any?
    fun write(index: Int, value: Any?)
}