package omuretu.typechecker

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment

sealed class Type {
    companion object {
        fun from(typeName: String): Type? {
            return when (typeName) {
                Defined.Any.NAME -> Defined.Any()
                Defined.Int.NAME -> Defined.Int()
                Defined.String.NAME -> Defined.String()
                NeedInference.NAME -> NeedInference()
                else -> null
            }
        }
    }

    sealed class Defined : Type() {
        class Any(override var readOnly: Boolean = true) : Defined() {
            companion object {
                const val NAME = "Any"
            }
        }

        class Int(override var readOnly: Boolean = true) : Defined() {
            companion object {
                const val NAME = "Int"
            }
        }

        class String(override var readOnly: Boolean = true) : Defined() {
            companion object {
                const val NAME = "String"
            }
        }

        class Array(
            val type: Defined,
            override var readOnly: Boolean = true
        ) : Defined() {
            companion object {
                const val NAME = "Array"
            }
        }

        class Range(override var readOnly: Boolean = true) : Defined() {
            companion object {
                const val NAME = "Range"
            }
        }

        class Unit(override var readOnly: Boolean = true) : Defined() {
            companion object {
                const val NAME = "Unit"
            }
        }

        class Class(
            val name: kotlin.String,
            private val typeEnvironment: TypeEnvironment,
            private val classMemberLocationMap: IdNameLocationMap,
            override var readOnly: Boolean = true
        ) : Defined() {
            companion object {
                const val NAME = "Class"
            }

            fun getMemberTypeOf(name: kotlin.String): Type? {
                return classMemberLocationMap.getLocationFromAllMap(name)?.let {
                    typeEnvironment.get(EnvironmentKey(it.ancestorAt, it.indexInIdNames))
                }
            }
        }

        class Object(
            val classs: Class,
            override var readOnly: Boolean = true
        ) : Defined() {
            companion object {
                const val NAME = "Object"
            }

            val name: kotlin.String
                get() = classs.name

            fun getMemberType(name: kotlin.String): Type? {
                return classs.getMemberTypeOf(name)
            }
        }

        class Function(val returnType: Defined, val parameterTypes: List<Defined> = listOf()) : Defined() {
            companion object {
                const val NAME = "Function"
            }
        }

        override fun toString(): kotlin.String = when (this) {
            is Any -> Any.NAME
            is Int -> Int.NAME
            is String -> String.NAME
            is Array -> Array.NAME
            is Range -> Range.NAME
            is Unit -> Unit.NAME
            is Class -> Class.NAME
            is Object -> Object.NAME
            is Function -> Function.NAME
        }
    }

    /**
     * 型推論が必要なType
     * 型が決まった時にプロパティのtypeに値が入る
     *
     * @property typeInferred
     */
    class NeedInference(var typeInferred: Defined? = null) : Type() {
        companion object {
            const val NAME = "NeedInference"
        }

        override var readOnly: Boolean = true
    }

    open var readOnly: Boolean = true
}
