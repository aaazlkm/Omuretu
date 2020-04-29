package omuretu.environment


/**
 * 出てきた変数名関数名を登録して、変数名が出現する位置(nest, index)を返す
 */
class NestedIdNameLocationMap(
        private val idNameLocationMapParent: NestedIdNameLocationMap? = null
) {
    private val idNameToIndex = hashMapOf<String, Int>()

    val idNamesSize: Int
        get() = idNameToIndex.size

    //region get methods

    fun getLocationFromOnlyThisMap(idName: String): Location? {
        return idNameToIndex[idName]?.let { Location(0, it) }
    }

    fun getLocationFromAllMap(idName: String): Location? {
        return getLocationAboveAncestorAt(idName, 0)
    }

    //endregion

    //region put methods

    fun putAndReturnLocation(idName: String): Location {
        return getLocationFromAllMap(idName) ?: registerAndCreateLocation(idName)
    }

    fun putOnlyThisMapAndReturnLocation(idName: String): Location {
        return idNameToIndex[idName]?.let { Location(0, it) } ?: registerAndCreateLocation(idName)
    }

    //endregion

    fun copyFrom(idNameLocationMapParent: NestedIdNameLocationMap) {
        this.idNameToIndex.putAll(idNameLocationMapParent.idNameToIndex)
    }

    private fun getLocationAboveAncestorAt(idName: String, ancestorAt: Int): Location? {
        val index = idNameToIndex[idName]
        return if (index == null) {
            idNameLocationMapParent?.getLocationAboveAncestorAt(idName, ancestorAt + 1)
        } else {
            Location(ancestorAt, index)
        }
    }

    private fun registerAndCreateLocation(idName: String): Location {
        val index = createNewIndex()
        idNameToIndex[idName] = index
        return Location(0, index)
    }

    private fun createNewIndex(): Int {
        return idNameToIndex.size
    }
}

data class Location(val ancestorAt: Int, val indexInIdNames: Int)
