package omuretu.environment

import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.exception.TypeException
import omuretu.typechecker.Type
import omuretu.util.ReflectionUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeEnvironmentImplTest {
    @Nested
    class WhenNotAncestor {
        private lateinit var typeEnvironment: TypeEnvironment

        @BeforeEach
        fun setup() {
            typeEnvironment = TypeEnvironmentImpl()
        }

        @Test
        fun get() {
            val environmentKey = EnvironmentKey(0, 0)
            assertEquals(null, typeEnvironment.get(environmentKey))
        }

        @Test
        fun put() {
            assertThrows<OmuretuException> {
                typeEnvironment.put(EnvironmentKey(-1, 0), Type.NeedInference())
            }
            assertThrows<OmuretuException> {
                typeEnvironment.put(EnvironmentKey(1, 0), Type.NeedInference())
            }
        }

        @Test
        fun putAndGetWhenIndex0() {
            val environmentKey = EnvironmentKey(0, 0)
            typeEnvironment.put(environmentKey, Type.Defined.Int())
            assertTrue { typeEnvironment.get(environmentKey)?.let { it::class } == Type.Defined.Int()::class }
        }

        @Test
        fun putAndGetWhenIndex11() {
            val environmentKey = EnvironmentKey(0, 11)
            typeEnvironment.put(environmentKey, Type.Defined.Int())
            assertTrue { typeEnvironment.get(environmentKey)?.let { it::class } == Type.Defined.Int()::class }
        }
    }

    @Nested
    class WhenAncestorExits {

        private lateinit var environmentKey: EnvironmentKey
        private lateinit var typeSaved: Type
        private lateinit var typeEnvironment: TypeEnvironment

        @BeforeEach
        fun setup() {
            val ancestor = TypeEnvironmentImpl()
            val environmentKey = EnvironmentKey(0, 0)
            val type = Type.Defined.Int()
            ancestor.put(environmentKey, type)
            this.environmentKey = environmentKey
            this.typeSaved = type
            this.typeEnvironment = TypeEnvironmentImpl(ancestor)
        }

        @Test
        fun get() {
            assertEquals(typeSaved, typeEnvironment.get(EnvironmentKey(1, environmentKey.index)))
            assertEquals(null, typeEnvironment.get(environmentKey))
        }

        @Test
        fun put() {
            val type = Type.Defined.String()
            val environmentKey = EnvironmentKey(1, 2)
            typeEnvironment.put(environmentKey, type)
            assertEquals(type, typeEnvironment.get(environmentKey))
        }
    }

    @Nested
    class EquationTest {
        private lateinit var typeEnvironment: TypeEnvironment

        @BeforeEach
        fun setup() {
            typeEnvironment = TypeEnvironmentImpl()
        }

        @Test
        fun addEquation() {
            val typeInference1 = Type.NeedInference(Type.Defined.Int())
            val typeInference2 = Type.NeedInference()
            assertThrows<TypeException> {
                typeEnvironment.addEquation(typeInference1, typeInference2)
            }
        }

        @Test
        fun defineEquationType() {
            val testType = Type.NeedInference()
            typeEnvironment.addEquation(Type.NeedInference(), Type.NeedInference())
            typeEnvironment.addEquation(Type.NeedInference(), Type.NeedInference())
            typeEnvironment.addEquation(Type.NeedInference(), Type.NeedInference())
            typeEnvironment.addEquation(testType, Type.NeedInference())
            val typeEquations = ReflectionUtil.pickValue<List<TypeEnvironmentImpl.TypeEquation>>(typeEnvironment, "typeEquations")
            assertEquals(4, typeEquations.size)
            typeEnvironment.defineEquationType(testType, Type.Defined.Int())
            assertEquals(3, typeEquations.size)
        }
    }
}
