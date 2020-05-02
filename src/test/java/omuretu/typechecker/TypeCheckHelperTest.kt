package omuretu.typechecker

import omuretu.environment.TypeEnvironmentImpl
import omuretu.exception.TypeException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import parser.ast.ASTList

internal class TypeCheckHelperTest {
    @ParameterizedTest
    @MethodSource("provideTestDataOfCheckSubTypeOf")
    fun checkSubTypeOrThrow(superType: Type, subType: Type, needError: Boolean) {
        val dummyASTTree = ASTList(listOf())
        val dummyEnvironment = TypeEnvironmentImpl()
        if (needError) {
            org.junit.jupiter.api.assertThrows<TypeException>("superType: $superType subType: $subType needError: $needError ")
            { TypeCheckHelper.checkSubTypeOrThrow(superType, subType, dummyASTTree, dummyEnvironment) }
        } else {
            assertDoesNotThrow { TypeCheckHelper.checkSubTypeOrThrow(superType, subType, dummyASTTree, dummyEnvironment) }
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestDataOfPlus")
    fun plus(type1: Type, type2: Type, expected: Type) {
        val dummyEnvironment = TypeEnvironmentImpl()
        assertTrue {
            expected::class == TypeCheckHelper.plus(type1, type2, dummyEnvironment)::class
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestDataOfUnion")
    fun union(type1: Type, type2: Type, expected: Type) {
        val dummyEnvironment = TypeEnvironmentImpl()
        assertTrue {
            expected::class == TypeCheckHelper.union(type1, type2, dummyEnvironment)::class
        }
    }

    companion object {
        @JvmStatic
        fun provideTestDataOfMatch(): List<Arguments> {
            val function1 = Type.Defined.Function(Type.Defined.String(), listOf(Type.Defined.String(), Type.Defined.Int()))
            val functionForReturnType = Type.Defined.Function(Type.Defined.Int(), listOf(Type.Defined.String(), Type.Defined.Int()))
            val functionForParameterType = Type.Defined.Function(Type.Defined.String(), listOf(Type.Defined.Int(), Type.Defined.Int()))
            val functionForSize = Type.Defined.Function(Type.Defined.String(), listOf(Type.Defined.Int(), Type.Defined.Int(), Type.Defined.Int()))
            return listOf(
                    Arguments.arguments(Type.Defined.Any, Type.Defined.Any, true),
                    Arguments.arguments(Type.Defined.Any, Type.Defined.String, false),
                    Arguments.arguments(Type.Defined.Int, Type.Defined.String, false),
                    Arguments.arguments(Type.Defined.String, Type.Defined.String, true),
                    Arguments.arguments(function1, Type.Defined.String, false),
                    Arguments.arguments(function1, function1, true),
                    Arguments.arguments(function1, functionForReturnType, false),
                    Arguments.arguments(function1, functionForParameterType, false),
                    Arguments.arguments(function1, functionForSize, false)

            )
        }

        @JvmStatic
        fun provideTestDataOfCheckSubTypeOf(): List<Arguments> {
            val typeNeedInference = Type.NeedInference()
            val typeNeedInferenceAny = Type.NeedInference(Type.Defined.Any())
            val typeNeedInferenceInt = Type.NeedInference(Type.Defined.Int())
            val typeNeedInferenceString = Type.NeedInference(Type.Defined.String())
            return listOf(
                    Arguments.arguments(Type.Defined.Any(), Type.Defined.Int(), false),
                    Arguments.arguments(Type.Defined.Int(), Type.Defined.Int(), false),
                    Arguments.arguments(Type.Defined.Int(), Type.Defined.Any(), true),
                    Arguments.arguments(Type.Defined.String(), Type.Defined.Int(), true),

                    Arguments.arguments(typeNeedInferenceAny, typeNeedInferenceInt, false),
                    Arguments.arguments(typeNeedInferenceInt, typeNeedInferenceInt, false),
                    Arguments.arguments(typeNeedInferenceInt, typeNeedInferenceAny, true),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInferenceInt, true),

                    Arguments.arguments(Type.Defined.Any(), typeNeedInferenceInt, false),
                    Arguments.arguments(Type.Defined.Int(), typeNeedInferenceInt, false),
                    Arguments.arguments(typeNeedInferenceInt, Type.Defined.Any(), true),
                    Arguments.arguments(typeNeedInferenceString, Type.Defined.Int(), true),

                    Arguments.arguments(typeNeedInference, typeNeedInferenceInt, false),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInference, false),

                    Arguments.arguments(typeNeedInference, typeNeedInference, false)
            )
        }

        @JvmStatic
        fun provideTestDataOfPlus(): List<Arguments> {
            val typeNeedInference = Type.NeedInference()
            val typeNeedInferenceAny = Type.NeedInference(Type.Defined.Any())
            val typeNeedInferenceInt = Type.NeedInference(Type.Defined.Int())
            val typeNeedInferenceString = Type.NeedInference(Type.Defined.String())
            return listOf(
                    Arguments.arguments(Type.Defined.Any(), Type.Defined.String(), Type.Defined.String()),
                    Arguments.arguments(Type.Defined.Int(), Type.Defined.Int(), Type.Defined.Int()),
                    Arguments.arguments(Type.Defined.Int(), Type.Defined.Any(), Type.Defined.Any()),
                    Arguments.arguments(Type.Defined.String(), Type.Defined.String(), Type.Defined.String()),

                    Arguments.arguments(typeNeedInferenceAny, typeNeedInferenceString, Type.Defined.String()),
                    Arguments.arguments(typeNeedInferenceInt, typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(typeNeedInferenceInt, typeNeedInferenceAny, Type.Defined.Any()),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInferenceString, Type.Defined.String()),

                    Arguments.arguments(Type.Defined.Any(), typeNeedInferenceString, Type.Defined.String()),
                    Arguments.arguments(Type.Defined.Int(), typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(Type.Defined.Int(), typeNeedInferenceAny, Type.Defined.Any()),
                    Arguments.arguments(Type.Defined.String(), typeNeedInferenceString, Type.Defined.String()),

                    Arguments.arguments(typeNeedInference, typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInference, Type.Defined.String()),

                    Arguments.arguments(typeNeedInference, typeNeedInference, typeNeedInference)
            )
        }

        @JvmStatic
        fun provideTestDataOfUnion(): List<Arguments> {
            val typeNeedInference = Type.NeedInference()
            val typeNeedInferenceAny = Type.NeedInference(Type.Defined.Any())
            val typeNeedInferenceInt = Type.NeedInference(Type.Defined.Int())
            val typeNeedInferenceString = Type.NeedInference(Type.Defined.String())
            return listOf(
                    Arguments.arguments(Type.Defined.Any(), Type.Defined.Int(), Type.Defined.Any()),
                    Arguments.arguments(Type.Defined.Int(), Type.Defined.Int(), Type.Defined.Int()),
                    Arguments.arguments(Type.Defined.String(), Type.Defined.String(), Type.Defined.String()),

                    Arguments.arguments(typeNeedInferenceAny, typeNeedInferenceInt, Type.Defined.Any()),
                    Arguments.arguments(typeNeedInferenceInt, typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInferenceString, Type.Defined.String()),

                    Arguments.arguments(Type.Defined.Any(), typeNeedInferenceInt, Type.Defined.Any()),
                    Arguments.arguments(Type.Defined.Int(), typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(Type.Defined.String(), typeNeedInferenceString, Type.Defined.String()),

                    Arguments.arguments(typeNeedInference, typeNeedInferenceInt, Type.Defined.Int()),
                    Arguments.arguments(typeNeedInferenceString, typeNeedInference, Type.Defined.String()),

                    Arguments.arguments(typeNeedInference, typeNeedInference, typeNeedInference)
            )
        }
    }
}