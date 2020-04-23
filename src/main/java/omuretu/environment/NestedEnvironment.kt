package omuretu.environment

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.HeapMemory

open class NestedEnvironment(
        numberOfIdNames: Int,
        private val outEnvironment: NestedEnvironment? = null
) : Environment, HeapMemory {
    var indexToValues = arrayOfNulls<Any>(numberOfIdNames)

    open val byteCodeStore: ByteCodeStore?
        get() = outEnvironment?.byteCodeStore

    //region Environment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        if (key.ancestorAt == 0) {
            indexToValues[key.index] = value
        } else {
            outEnvironment?.put(EnvironmentKey(key.ancestorAt - 1, key.index), value)
        }
    }

    override fun get(key: EnvironmentKey): Any? {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        return if (key.ancestorAt == 0) {
            indexToValues[key.index]
        } else {
            outEnvironment?.get(EnvironmentKey(key.ancestorAt - 1, key.index))
        }
    }

    //endregion

    //region heap memory

    override fun read(index: Int): Any? {
        return indexToValues[index]
    }

    override fun write(index: Int, value: Any?) {
        indexToValues[index] = value
    }

    //endregion
}