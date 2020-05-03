package omuretu.virtualmachine.opecode

import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode

/**
 * bconst int8 reg
 *
 * @property virtualMachineStatus
 */
class BConstOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val INT_AT = 1
        const val REGISTER_AT = 2
        const val PROGRAM_LENGTH = 3

        fun createByteCode(value: Byte, registerAt: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.BCONST.byte)
                        add(value)
                        add(registerAt)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        registers[registerIndex] = code[programCounter + INT_AT]
        programCounter += PROGRAM_LENGTH
    }
}
