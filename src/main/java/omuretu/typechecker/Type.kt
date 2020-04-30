package omuretu.typechecker

sealed class Type {
    companion object {
        fun from(typeName: String): Type? {
            return when (typeName) {
                Defined.Any.NAME -> Defined.Any
                Defined.Int.NAME -> Defined.Int
                Defined.String.NAME -> Defined.String
                NeedInference.NAME -> NeedInference()
                else -> null
            }
        }
    }

    sealed class Defined : Type() {
        object Any : Defined() {
            const val NAME = "Any"
        }

        object Int : Defined() {
            const val NAME = "Int"
        }

        object String : Defined() {
            const val NAME = "String"
        }

        object Unit : Defined() {
            const val NAME = "Unit"
        }

        data class Function(val returnType: Defined, val parameterTypes: List<Defined> = listOf()) : Defined()
    }

    /**
     * 型推論が必要なType
     * 型が決まった時にプロパティのtypeに値が入る
     *
     * @property typeInferred
     */
    class NeedInference(typeInferred: Defined? = null) : Type() {
        companion object {
            const val NAME = "NeedInference"
        }

        var typeInferred = typeInferred
    }
}
