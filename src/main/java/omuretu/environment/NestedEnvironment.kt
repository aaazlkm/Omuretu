package omuretu.environment

import omuretu.exception.OmuretuException

open class NestedEnvironment(
        numberOfIdNames: Int,
        private val outEnvironment: NestedEnvironment? = null
) : Environment {
    var idNamesToValue = arrayOfNulls<Any>(numberOfIdNames)

    //region Environment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        if (key.ancestorAt == 0) {
            idNamesToValue[key.index] = value
        } else {
            outEnvironment?.put(EnvironmentKey(key.ancestorAt - 1, key.index), value)
        }
    }

    override fun get(key: EnvironmentKey): Any? {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        return if (key.ancestorAt == 0) {
            idNamesToValue[key.index]
        } else {
            outEnvironment?.get(EnvironmentKey(key.ancestorAt - 1, key.index))
        }
    }

    //endregion
}