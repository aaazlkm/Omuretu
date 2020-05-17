package omuretu.virtualmachine

import kotlin.experimental.and
import omuretu.exception.OmuretuException

class OmuretuVirtualMachine(
    configuration: Configuration
) {
    companion object {
        const val DEFAULT_CODE_SIZE = 100000
        const val DEFAULT_STACK_SIZE = 100000
        const val DEFAULT_STRINGS_SIZE = 1000

        const val NUMBER_OF_REGISTER = 6
        // プラス2はframePointer と returnPointer の分
        const val SAVE_AREA_SIZE = NUMBER_OF_REGISTER + 2

        // OmuretuVertualMachineではレジスタ番号0, 1, 2を -1, -2, -3で表している
        val numberToRegisterIndex = mapOf(
                0 to (-1).toByte(),
                1 to (-2).toByte(),
                2 to (-3).toByte(),
                3 to (-4).toByte(),
                4 to (-5).toByte(),
                5 to (-6).toByte(),
                6 to (-7).toByte()
        )

        fun encodeRegisterIndex(number: Int): Byte {
            return numberToRegisterIndex[number]
                    ?: throw OmuretuException("There is no register such a register number: $number. number of register is $NUMBER_OF_REGISTER")
        }

        fun decodeRegisterIndex(operand: Byte): Int {
            return numberToRegisterIndex.entries.firstOrNull { it.value == operand }?.key
                    ?: throw OmuretuException("There is no register such a register operand: $operand. number of register is $NUMBER_OF_REGISTER")
        }

        fun isRegister(operand: Byte): Boolean {
            return operand < 0
        }

        fun readInt(array: ByteArray): Int? {
            if (array.size != 4) return null
            return (array[0].toInt() shl 24) or
                    ((array[1] and 0xff.toByte()).toInt() shl 16) or
                    ((array[2] and 0xff.toByte()).toInt() shl 8) or
                    (array[3] and 0xff.toByte()).toInt()
        }

        fun readShort(array: ByteArray): Int? {
            if (array.size != 2) return null
            return array[0].toInt() shl 8 or
                    (array[1].toInt() and 0xff)
        }
    }

    class Configuration(
        val stackSize: Int = DEFAULT_STACK_SIZE,
        val heapMemory: HeapMemory
    )

    class Status(
        var code: ByteArray,
        var stack: Array<Any?>,
        var heapMemory: HeapMemory,
        var strings: Array<String>,
        var programCounter: Int,
        var framePointer: Int,
        var stackPointer: Int,
        var returnPointer: Int,
        var registers: Array<Any?>
    )

    private lateinit var status: Status

    val stack = Array<Any?>(configuration.stackSize) { null }
    var heapMemory = configuration.heapMemory

    fun run(byteCodeStore: ByteCodeStore, entry: Int) {

        this.status = Status(
                byteCodeStore.byteCode,
                stack,
                heapMemory,
                byteCodeStore.strings,
                entry,
                0,
                0,
                -1,
                Array<Any?>(NUMBER_OF_REGISTER) { null }
        )

        while (status.programCounter >= 0) {
            runMainLoop()
        }
    }

    private fun runMainLoop() {
        val opecodeByte = status.code[status.programCounter]
        val opecode = OpecodeDefinition.form(opecodeByte) ?: throw OmuretuException("undefined opecode ${status.code[status.programCounter]}")
        val opecodeRunner = opecode.createRunner(status)
        opecodeRunner.run()
    }
}
