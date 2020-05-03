package omuretu.virtualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode

/**
 * move src dest
 *
 * @property virtualMachineStatus
 */
class MoveOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val SRC_AT = 1
        const val DEST_AT = 2
        const val PROGRAM_LENGTH = 3

        fun createByteCode(src: Byte, dest: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.MOVE.byte)
                        add(src)
                        add(dest)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val src = code[programCounter + SRC_AT]
        val dest = code[programCounter + DEST_AT]

        when {
            OmuretuVirtualMachine.isRegister(src) && !OmuretuVirtualMachine.isRegister(dest) -> {
                val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(src)
                val value = registers[registerIndex]
                val variableAt = dest.toInt()
                stack[framePointer + variableAt] = value
            }
            !OmuretuVirtualMachine.isRegister(src) && OmuretuVirtualMachine.isRegister(dest) -> {
                val variableAt = src.toInt()
                val value = stack[framePointer + variableAt]
                val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(dest)
                registers[registerIndex] = value
            }
            else -> {
                throw OmuretuException("cannot move variable")
            }
        }

        programCounter += PROGRAM_LENGTH
    }
}
