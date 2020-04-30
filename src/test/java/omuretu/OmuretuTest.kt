package omuretu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.*

internal class OmuretuTest {
    private val pathToTestCaseDir = "${System.getProperty("user.dir")}/src/test/resources"

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
        OmuretuRunner.run(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testVal() {
        // TODO 実装すること
    }

    @Test
    fun testOperator() {
        val path = "$pathToTestCaseDir/test_operator"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        OmuretuRunner.run(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testArray() {
        // TODO 実装すること
    }

    @Test
    fun testIf() {
        val path = "$pathToTestCaseDir/test_if"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        OmuretuRunner.run(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testWhile() {
        val path = "$pathToTestCaseDir/test_while"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        OmuretuRunner.run(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testFor() {
        // TODO 実装すること
    }

    @Test
    fun testDef() {
        val path = "$pathToTestCaseDir/test_def"
        val reader = BufferedReader(FileReader(path))
        val expected = reader.readLine().split(" ")
        OmuretuRunner.run(reader)
        val result = out.toString().split("\n").filter { it.isNotEmpty() }
        assertEquals(expected, result)
    }

    @Test
    fun testClass() {
        // TODO 実装すること
    }
}