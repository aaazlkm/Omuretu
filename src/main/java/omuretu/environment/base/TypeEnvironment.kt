package omuretu.environment.base

import omuretu.typechecker.Type

interface TypeEnvironment: Environment {
    fun put(key: EnvironmentKey, type: Type)

    fun get(key: EnvironmentKey): Type?

    fun addEquation(typeNeedInference1: Type.NeedInference, typeNeedInference2: Type.NeedInference)

    fun defineEquationType(target: Type.NeedInference, typeDefined: Type.Defined)
}