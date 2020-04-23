package omuretu.environment

import omuretu.NestedIdNameLocationMap
import omuretu.vertualmachine.ByteCodeStore
import omuretu.vertualmachine.HeapMemory
import omuretu.vertualmachine.OmuretuVirtualMachine

class GlobalEnvironment : NestedEnvironment(10), HeapMemory {
    val idNameLocationMap = NestedIdNameLocationMap()

    val omuretuVirtualMachine = OmuretuVirtualMachine(
            OmuretuVirtualMachine.Configuration(
                    heapMemory = this
            )
    )

    override val byteCodeStore =  ByteCodeStore()

    //region NestedEnvironment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt == 0) {
            putValueByIndex(key.index, value)
        } else {
            super.put(key, value)
        }
    }

    //endregion

    //region HeapMemory override methods

    override fun read(index: Int): Any? {
        return indexToValues[index]
    }

    override fun write(index: Int, value: Any?) {
        indexToValues[index] = value
    }

    //endregion

    fun getValueByIdName(idName: String): Any? {
        return idNameLocationMap.getLocationFromAllMap(idName)?.let {
            indexToValues.getOrNull(it.indexInIdNames)
        }
    }

    fun putValueByIdName(idName: String, value: Any) {
        val location = idNameLocationMap.putAndReturnLocation(idName)
        putValueByIndex(location.indexInIdNames, value)
    }

    /**
     * 大域変数の数はこのクラス生成時にわからないので、このメソッドで動的に配列の要素を確保して変数を追加する
     *
     * @param index インデックス
     * @param value 変数の値
     */
    private fun putValueByIndex(index: Int, value: Any) {
        if (index >= indexToValues.size)  {
            val newLength = index - indexToValues.size
            indexToValues = indexToValues.copyOf(newLength + 10).mapNotNull { it }.toTypedArray() // 10個分余分に配列を確保しておく
        }
        indexToValues[index] = value
    }
}