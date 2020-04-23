package omuretu.vertualmachine.opecode

import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode

/**
 * save int8 (関数で使用する引数と局所変数の数)
 *
 * @property virtualMachineStatus
 */
class SaveOpecode (
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val INT_AT = 1
        const val PROGRAM_LENGTH = 2

        fun createByteCode(sizeOfVariablesInFunction: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.SAVE.byte)
                        add(sizeOfVariablesInFunction)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val sizeOfVariablesInFunction = code[programCounter + INT_AT].toInt()
        var dest = stackPointer + sizeOfVariablesInFunction

        // レジスタの値を待避
        for (i in 0 until OmuretuVirtualMachine.NUMBER_OF_REGISTER) {
            stack[dest++] = registers[i]
        }
        stack[dest++] = framePointer
        stack[dest] = returnPointer

        framePointer = stackPointer
        stackPointer += sizeOfVariablesInFunction + OmuretuVirtualMachine.SAVE_AREA_SIZE

        programCounter += PROGRAM_LENGTH
    }
}