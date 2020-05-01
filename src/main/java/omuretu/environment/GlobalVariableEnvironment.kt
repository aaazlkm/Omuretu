package omuretu.environment

import omuretu.environment.base.EnvironmentKey
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.HeapMemory
import omuretu.vertualmachine.OmuretuVirtualMachine

class GlobalVariableEnvironment : NestedVariableEnvironment(10), HeapMemory {
    val idNameLocationMap = IdNameLocationMap()

    val omuretuVirtualMachine = OmuretuVirtualMachine(
            OmuretuVirtualMachine.Configuration(
                    heapMemory = this
            )
    )

    override val byteCodeStore = ByteCodeStore()

    //region NestedEnvironment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt == 0) {
            putValueAndExpandIfNeeded(key.index, value)
        } else {
            super.put(key, value)
        }
    }

    //endregion

    //region HeapMemory override methods

    override fun read(index: Int): Any? {
        return values[index]
    }

    override fun write(index: Int, value: Any?) {
        values[index] = value
    }

    //endregion

    fun getValueByIdName(idName: String): Any? {
        return idNameLocationMap.getLocationFromAllMap(idName)?.let {
            values.getOrNull(it.indexInIdNames)
        }
    }

    fun putValueByIdName(idName: String, value: Any) {
        val location = idNameLocationMap.putAndReturnLocation(idName)
        putValueAndExpandIfNeeded(location.indexInIdNames, value)
    }

    /**
     * 大域変数の数は実行時にしかわからないので、このメソッドで動的に配列の要素を確保して変数を追加する
     *
     * @param index インデックス
     * @param value 変数の値
     */
    private fun putValueAndExpandIfNeeded(index: Int, value: Any) {
        if (index >= values.size) {
            val newLength = index - values.size + 10 // 10個分余分に配列を確保しておく
            values = values.copyOf(values.size + newLength).map { it }.toTypedArray()
        }
        values[index] = value
    }
}