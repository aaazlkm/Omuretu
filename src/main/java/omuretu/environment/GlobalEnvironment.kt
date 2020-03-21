package omuretu.environment

import omuretu.NestedIdNameLocationMap

class GlobalEnvironment : NestedEnvironment(10) {
    val idNameLocationMap = NestedIdNameLocationMap()

    //region NestedEnvironment

    override fun put(key: EnvironmentKey, value: Any) {
        if (key.ancestorAt == 0) {
            putValueByIndex(key.index, value)
        } else {
            super.put(key, value)
        }
    }

    //endregion

    fun getValueByIdName(idName: String): Any? {
        return idNameLocationMap.getLocationFromAllMap(idName)?.let {
            idNamesToValue.getOrNull(it.indexInIdNames)
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
        if (index >= idNamesToValue.size)  {
            val newLength = index - idNamesToValue.size
            idNamesToValue = idNamesToValue.copyOf(newLength + 10).mapNotNull { it }.toTypedArray() // 10個分余分に配列を確保しておく
        }
        idNamesToValue[index] = value
    }
}