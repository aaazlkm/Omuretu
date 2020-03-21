package omuretu


/**
 * 出てきた変数名関数名を登録して、変数名が出現する位置(nest, index)を返す
 */
class NestedIdNameLocationMap(
        private val idNameLocationMapParent: NestedIdNameLocationMap? = null
) {
    private val idNameToIndex = hashMapOf<String, Int>()

    val idNamesSize: Int
        get() = idNameToIndex.size

    fun getLocationFromAllMap(idName: String): Location? {
        return getLocationAboveAncestorAt(idName, 0)
    }

    fun getLocationAboveAncestorAt(idName: String, ancestorAt: Int): Location? {
        val index = idNameToIndex[idName]
        return if (index == null) {
            idNameLocationMapParent?.getLocationAboveAncestorAt(idName, ancestorAt + 1)
        } else {
            Location(ancestorAt, index)
        }
    }

    fun putAndReturnLocation(idName: String): Location {
        return getLocationFromAllMap(idName) ?: createAndAddLocation(idName)
    }

    fun putOnlyThisMapAndReturnLocation(idName: String): Location {
        return idNameToIndex[idName]?.let { Location(0, it) } ?: createAndAddLocation(idName)
    }

    private fun createAndAddLocation(idName: String): Location {
        val index = idNameToIndex.size
        idNameToIndex[idName] = index
        return Location(0, index)
    }
}

data class Location(val ancestorAt: Int, val indexInIdNames: Int)
