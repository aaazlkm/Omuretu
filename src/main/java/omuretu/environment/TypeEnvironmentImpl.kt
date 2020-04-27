package omuretu.environment

import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.exception.TypeException
import omuretu.typechecker.Type

class TypeEnvironmentImpl(
        private val outEnvironment: TypeEnvironment? = null
) : TypeEnvironment {
    data class TypeEquation(val type1: Type.NeedInference, val type2: Type.NeedInference)

    private var types = arrayOfNulls<Type>(10)
    private val typeEquations = mutableListOf<TypeEquation>()

    //region put methods

    override fun put(key: EnvironmentKey, type: Type) {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        if (key.ancestorAt == 0) {
            putValueAndExpandIfNeeded(key.index, type)
        } else {
            outEnvironment?.put(EnvironmentKey(key.ancestorAt - 1, key.index), type) ?: run {
                throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
            }
        }
    }

    /**
     * 大域変数の数は実行時にしかわからないので、このメソッドで動的に配列の要素を確保して変数を追加する
     *
     * @param index
     * @param type
     */
    private fun putValueAndExpandIfNeeded(index: Int, type: Type) {
        if (index >= types.size) {
            val newLength = index - types.size + 10 // 10個分余分に配列を確保しておく
            types = types.copyOf(types.size + newLength).map { it }.toTypedArray()
        }
        types[index] = type
    }

    //endregion

    override fun get(key: EnvironmentKey): Type? {
        if (key.ancestorAt < 0) throw OmuretuException("illegal ancestorAt: ${key.ancestorAt}")
        return if (key.ancestorAt == 0) {
            types[key.index]
        } else {
            outEnvironment?.get(EnvironmentKey(key.ancestorAt - 1, key.index))
        }
    }

    /**
     * ここで渡すTypeは型推論できていないものに限る
     *
     * @param typeNeedInference1
     * @param typeNeedInference2
     */
    override fun addEquation(typeNeedInference1: Type.NeedInference, typeNeedInference2: Type.NeedInference) {
        if (typeNeedInference1.typeInferred != null || typeNeedInference2.typeInferred != null) throw TypeException("each type should not be defined")
        typeEquations.add(TypeEquation(typeNeedInference1, typeNeedInference2))
    }

    override fun deifneEquatationType(target: Type.NeedInference, typeDefined: Type.Defined) {
        val targets = typeEquations.filter { it.type1 == target || it.type2 == target }
        targets.forEach {
            it.type1.typeInferred = typeDefined
            it.type2.typeInferred = typeDefined
        }
        typeEquations.removeAll(targets)
    }
}