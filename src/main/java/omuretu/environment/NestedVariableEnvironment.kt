package omuretu.environment

import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.HeapMemory

open class NestedVariableEnvironment(
        numberOfIdNames: Int,
        private val outEnvironment: NestedVariableEnvironment? = null
) : VariableEnvironment, HeapMemory {
    protected var values = arrayOfNulls<Any>(numberOfIdNames)

    open val byteCodeStore: ByteCodeStore?
        get() = outEnvironment?.byteCodeStore

    //region Environment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        if (key.ancestorAt == 0) {
            values[key.index] = value
        } else {
            outEnvironment?.put(EnvironmentKey(key.ancestorAt - 1, key.index), value) ?: run {
                throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
            }
        }
    }

    override fun get(key: EnvironmentKey): Any? {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        return if (key.ancestorAt == 0) {
            values[key.index]
        } else {
            outEnvironment?.get(EnvironmentKey(key.ancestorAt - 1, key.index))
        }
    }

    //endregion

    //region heap memory

    override fun read(index: Int): Any? {
        return values[index]
    }

    override fun write(index: Int, value: Any?) {
        values[index] = value
    }

    //endregion
}