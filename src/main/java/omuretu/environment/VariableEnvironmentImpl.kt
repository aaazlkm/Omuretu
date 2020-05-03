package omuretu.environment

import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.virtualmachine.ByteCodeStore
import omuretu.virtualmachine.HeapMemory

open class VariableEnvironmentImpl(
    numberOfIdNames: Int,
    private val outEnvironmentImpl: VariableEnvironmentImpl? = null
) : VariableEnvironment, HeapMemory {
    protected var values = arrayOfNulls<Any>(numberOfIdNames)

    open val byteCodeStore: ByteCodeStore?
        get() = outEnvironmentImpl?.byteCodeStore

    //region Environment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        if (key.ancestorAt == 0) {
            values[key.index] = value
        } else {
            outEnvironmentImpl?.put(EnvironmentKey(key.ancestorAt - 1, key.index), value) ?: run {
                throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
            }
        }
    }

    override fun get(key: EnvironmentKey): Any? {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        return if (key.ancestorAt == 0) {
            values[key.index]
        } else {
            outEnvironmentImpl?.get(EnvironmentKey(key.ancestorAt - 1, key.index))
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
