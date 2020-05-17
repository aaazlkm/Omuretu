package omuretu

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileReader
import java.io.PrintStream
import java.io.Reader
import lexer.token.Token
import omuretu.ast.statement.NullStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.TypeEnvironmentImpl
import omuretu.exception.OmuretuException
import omuretu.exception.TypeException
import omuretu.native.NativeFunctionEnvironmentFactory
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class OmuretuTest {
    companion object {
        private val pathToTestCaseDir = "${System.getProperty("user.dir")}/src/test/resources"

        fun runForTest(reader: Reader) {
            val parser = OmuretuParser()
            val typeEnvironment = TypeEnvironmentImpl()
            val variableEnvironment = NativeFunctionEnvironmentFactory.createBasedOn(GlobalVariableEnvironment(), typeEnvironment)

            val lexer = OmuretuLexer(reader)
            while (lexer.readTokenAt(0) !== Token.EOF) {
                val t = parser.parse(lexer)
                if (t !is NullStatement) {
                    t.accept(IdNameLocationVisitor, variableEnvironment.idNameLocationMap)
                    val type = t.accept(CheckTypeVisitor, typeEnvironment)
                    val result = t.accept(EvaluateVisitor, variableEnvironment)
                }
            }
        }
    }

    private lateinit var out: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        out = ByteArrayOutputStream()
        System.setOut(PrintStream(out))
    }

    @Test
    fun testVar() {
        val path = "$pathToTestCaseDir/test_var"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        runForTest(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testVal() {
        val path = "$pathToTestCaseDir/val/test_val"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        runForTest(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testValError() {
        val path = "$pathToTestCaseDir/val/test_val_error"
        val reader = BufferedReader(FileReader(path))
        assertThrows<OmuretuException> {
            runForTest(reader)
        }
    }

    @Test
    fun testOperator() {
        val path = "$pathToTestCaseDir/test_operator"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        runForTest(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Nested
    class Array {
        private lateinit var out: ByteArrayOutputStream

        @BeforeEach
        fun setup() {
            out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))
        }

        @Test
        fun testArray() {
            val path = "$pathToTestCaseDir/array/test_array"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testArrayAccess() {
            val path = "$pathToTestCaseDir/array/test_array_access"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testArrayIndexError() {
            val path = "$pathToTestCaseDir/array/test_array_index_error"
            val reader = BufferedReader(FileReader(path))
            assertThrows<IndexOutOfBoundsException> {
                runForTest(reader)
            }
        }

        @Test
        fun testArrayTypeAccessError() {
            val path = "$pathToTestCaseDir/array/test_array_type_access_error"
            val reader = BufferedReader(FileReader(path))
            assertThrows<TypeException> {
                runForTest(reader)
            }
        }

        @Test
        fun testArrayTypeError() {
            val path = "$pathToTestCaseDir/array/test_array_type_error"
            val reader = BufferedReader(FileReader(path))
            assertThrows<TypeException> {
                runForTest(reader)
            }
        }
    }

    @Nested
    class For {
        private lateinit var out: ByteArrayOutputStream

        @BeforeEach
        fun setup() {
            out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))
        }

        @Test
        fun testFor() {
            val path = "$pathToTestCaseDir/for/test_for"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testForDown() {
            val path = "$pathToTestCaseDir/for/test_for_down"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testForStep() {
            val path = "$pathToTestCaseDir/for/test_for_step"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testForStepError() {
            val path = "$pathToTestCaseDir/for/test_for_step_error"
            val reader = BufferedReader(FileReader(path))
            assertThrows<OmuretuException> {
                runForTest(reader)
            }
        }
    }

    @Nested
    class If {
        private lateinit var out: ByteArrayOutputStream

        @BeforeEach
        fun setup() {
            out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))
        }

        @Test
        fun testIf() {
            val path = "$pathToTestCaseDir/if/test_if"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testIfError() {
            val path = "$pathToTestCaseDir/if/test_if_error"
            val reader = BufferedReader(FileReader(path))
            assertThrows<OmuretuException> {
                runForTest(reader)
            }
        }

        @Test
        fun testElseIf() {
            val path = "$pathToTestCaseDir/if/test_elseif"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testElseIfWhenFizzBuzz() {
            val path = "$pathToTestCaseDir/if/test_elif_fizz_buzz"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }
    }

    @Test
    fun testWhile() {
        val path = "$pathToTestCaseDir/while/test_while"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        runForTest(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testWhileError() {
        val path = "$pathToTestCaseDir/while/test_while_error"
        val reader = BufferedReader(FileReader(path))
        assertThrows<OmuretuException> {
            runForTest(reader)
        }
    }

    @Test
    fun testDef() {
        val path = "$pathToTestCaseDir/test_def"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        runForTest(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Nested
    class Class {
        private lateinit var out: ByteArrayOutputStream

        @BeforeEach
        fun setup() {
            out = ByteArrayOutputStream()
            System.setOut(PrintStream(out))
        }

        @Test
        fun testClassAccess() {
            val path = "$pathToTestCaseDir/class/test_class_access"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }

        @Test
        fun testClassFib() {
            val path = "$pathToTestCaseDir/class/test_class_fib"
            val reader = BufferedReader(FileReader(path))
            val expected = reader.readLine().split(" ")
            runForTest(reader)
            val result = out.toString().split("\n").filter { it.isNotEmpty() }
            assertEquals(expected, result)
        }
    }
}
