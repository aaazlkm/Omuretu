package util.ex

import omuretu.typechecker.Type

fun Pair<Type, Type>.fold(
    doOnDefineDefine: (Pair<Type.Defined, Type.Defined>) -> Type,
    doOnDefineInfer: (Pair<Type.Defined, Type.NeedInference>) -> Type,
    doOnInferInfer: (Pair<Type.NeedInference, Type.NeedInference>) -> Type
): Type {
    val (type1, type2) = this
    return when (type1) {
        is Type.Defined -> {
            when (type2) {
                is Type.Defined -> doOnDefineDefine(type1 to type2)
                is Type.NeedInference -> doOnDefineInfer(type1 to type2)
            }
        }
        is Type.NeedInference -> {
            when (type2) {
                is Type.Defined -> doOnDefineInfer(type2 to type1)
                is Type.NeedInference -> doOnInferInfer(type1 to type2)
            }
        }
    }
}
