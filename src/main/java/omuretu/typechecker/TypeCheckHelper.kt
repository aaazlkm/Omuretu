package omuretu.typechecker

import omuretu.environment.base.TypeEnvironment
import omuretu.exception.TypeException
import parser.ast.ASTTree
import util.ex.fold

object TypeCheckHelper {
    //region checkSubTypeOrThrow methods

    fun checkSubTypeOrThrow(superType: Type, subType: Type, where: ASTTree, environment: TypeEnvironment) {
        when (superType) {
            is Type.Defined -> {
                when (subType) {
                    is Type.Defined -> checkSubTypeOrThrow(superType, subType, where)
                    is Type.NeedInference -> checkSubTypeOrThrow(superType, subType, where, environment)
                }
            }
            is Type.NeedInference -> {
                when (subType) {
                    is Type.Defined -> checkSubTypeOrThrow(superType, subType, where, environment)
                    is Type.NeedInference -> checkSubTypeOrThrow(superType, subType, where, environment)
                }
            }
        }
    }

    private fun checkSubTypeOrThrow(superType: Type.Defined, subType: Type.Defined, where: ASTTree) {
        if (!checkSubTypeOf(superType, subType)) throw TypeException("type mismatch: cannot convert from $subType to $superType", where)
    }

    private fun checkSubTypeOrThrow(superType: Type.Defined, subType: Type.NeedInference, where: ASTTree, environment: TypeEnvironment) {
        return subType.typeInferred?.let {
            checkSubTypeOrThrow(superType, it, where)
        } ?: run {
            environment.defineEquationType(subType, superType)
            checkSubTypeOrThrow(superType, superType, where)
        }
    }

    private fun checkSubTypeOrThrow(superType: Type.NeedInference, subType: Type.Defined, where: ASTTree, environment: TypeEnvironment) {
        return superType.typeInferred?.let {
            checkSubTypeOrThrow(it, subType, where)
        } ?: run {
            environment.defineEquationType(superType, subType)
            checkSubTypeOrThrow(subType, subType, where)
        }
    }

    private fun checkSubTypeOrThrow(superType: Type.NeedInference, subType: Type.NeedInference, where: ASTTree, environment: TypeEnvironment) {
        superType.typeInferred?.let {
            return checkSubTypeOrThrow(it, subType, where, environment)
        }

        subType.typeInferred?.let {
            return checkSubTypeOrThrow(superType, it, where, environment)
        }

        // ここに入るならば、どちらもNeedInferenceでnullの値をもつ
        // environmentに追加
        if (superType.typeInferred == null && subType.typeInferred == null) {
            environment.addEquation(superType, subType)
        }
    }

    //endregion

    //region plus methods

    fun plus(type1: Type, type2: Type, environment: TypeEnvironment): Type {
        return (type1 to type2).fold(
                doOnDefineDefine = { plus(it.first, it.second) },
                doOnDefineInfer = { plus(it.first, it.second, environment) },
                doOnInferInfer = { plus(it.first, it.second, environment) }
        )
    }

    private fun plus(typeDefined1: Type.Defined, typeDefined2: Type.Defined): Type {
        return when {
            match(typeDefined1, Type.Defined.Int()) && match(typeDefined2, Type.Defined.Int()) -> Type.Defined.Int()
            match(typeDefined1, Type.Defined.String()) || match(typeDefined2, Type.Defined.String()) -> Type.Defined.String()
            else -> Type.Defined.Any()
        }
    }

    private fun plus(typeDefined: Type.Defined, typeInference: Type.NeedInference, environment: TypeEnvironment): Type {
        return typeInference.typeInferred?.let {
            plus(it, typeDefined)
        } ?: run {
            // environment更新処理下記を実行する
            environment.defineEquationType(typeInference, typeDefined)
            plus(typeDefined, typeDefined)
        }
    }

    private fun plus(typeInference1: Type.NeedInference, typeInference2: Type.NeedInference, environment: TypeEnvironment): Type {
        typeInference1.typeInferred?.let {
            return plus(it, typeInference2, environment)
        }

        typeInference2.typeInferred?.let {
            return plus(it, typeInference1, environment)
        }
        if (typeInference1.typeInferred == null && typeInference2.typeInferred == null) {
            environment.addEquation(typeInference1, typeInference2)
        }
        return typeInference1
    }

    //endregion

    //region union

    fun union(type1: Type, type2: Type, environment: TypeEnvironment): Type {
        return (type1 to type2).fold(
                doOnDefineDefine = { union(it.first, it.second) },
                doOnDefineInfer = { union(it.first, it.second, environment) },
                doOnInferInfer = { union(it.first, it.second, environment) }
        )
    }

    private fun union(typeDefined1: Type.Defined, typeDefined2: Type.Defined): Type {
        return if (match(typeDefined1, typeDefined2)) {
            typeDefined1
        } else {
            Type.Defined.Any()
        }
    }

    private fun union(typeDefined: Type.Defined, typeInference: Type.NeedInference, environment: TypeEnvironment): Type {
        return typeInference.typeInferred?.let {
            union(it, typeDefined)
        } ?: run {
            // environmentに保存してある型を更新
            environment.defineEquationType(typeInference, typeDefined)
            union(typeDefined, typeDefined)
        }
    }

    private fun union(typeInference1: Type.NeedInference, typeInference2: Type.NeedInference, environment: TypeEnvironment): Type {
        typeInference1.typeInferred?.let {
            return union(it, typeInference2, environment)
        }

        typeInference2.typeInferred?.let {
            return union(it, typeInference1, environment)
        }

        if (typeInference1.typeInferred == null && typeInference2.typeInferred == null) {
            environment.addEquation(typeInference1, typeInference2)
        }
        return typeInference1
    }

    //endregion

    private fun match(type1: Type, type2: Type): Boolean {
        return when (type1) {
            is Type.Defined.Function -> {
                if (type2 !is Type.Defined.Function) return false
                if (type1.parameterTypes.size != type2.parameterTypes.size) return false
                type1.parameterTypes.zip(type2.parameterTypes).filter { !match(it.first, it.second) }.let { if (it.isNotEmpty()) return false }
                return match(type1.returnType, type2.returnType)
            }
            else -> type1::class == type2::class
        }
    }

    //region check sub type

    private fun checkSubTypeOf(superType: Type.Defined, subType: Type.Defined): Boolean {
        return when (superType) {
            is Type.Defined.Array -> {
                checkSubTypeOfWhenSuperTypeArray(superType, subType)
            }
            is Type.Defined.Object -> {
                checkSubTypeOfWhenSuperTypeClass(superType, subType)
            }
            else -> {
                superType::class == subType::class || superType::class == Type.Defined.Any::class
            }
        }
    }

    private fun checkSubTypeOfWhenSuperTypeArray(superType: Type.Defined.Array, subType: Type.Defined): Boolean {
        return when (subType) {
            is Type.Defined.Array -> {
                checkSubTypeOf(superType.type, subType.type)
            }
            else -> {
                false
            }
        }
    }

    private fun checkSubTypeOfWhenSuperTypeClass(superType: Type.Defined.Object, subType: Type.Defined): Boolean {
        return when (subType) {
            is Type.Defined.Object -> {
                superType.name == subType.name
            }
            else -> {
                false
            }
        }
    }

    //endregion
}
