package omuretu.virtualmachine.opecode

import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode

/**
 * restore int8 (関数で使用する引数と局所変数の数)
 *
 * @property virtualMachineStatus
 */
class RestoreOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val SIZE_OF_VARIABLES_IN_FUNCTION_AT = 1
        const val PROGRAM_LENGTH = 2

        fun createByteCode(sizeOfVariablesInFunction: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.RESTORE.byte)
                        add(sizeOfVariablesInFunction)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val sizeOfVariablesInFunction = code[programCounter + SIZE_OF_VARIABLES_IN_FUNCTION_AT].toInt()
        var dest = framePointer + sizeOfVariablesInFunction
        for (i in 0 until OmuretuVirtualMachine.NUMBER_OF_REGISTER) {
            registers[i] = stack[dest++]
        }
        stackPointer = framePointer
        framePointer = stack[dest++] as Int
        returnPointer = stack[dest] as Int

        programCounter += PROGRAM_LENGTH
    }
}
