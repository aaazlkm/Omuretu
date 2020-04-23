package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode

/**
 * neg reg
 *
 * @property virtualMachineStatus
 */
class NegOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val REGISTER_AT = 1
        const val PROGRAM_LENGTH = 2

        fun createByteCode(registerAt: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.NEG.byte)
                        add(registerAt)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        val value = registers[registerIndex]
        if (value is Number) {
            registers[registerIndex] = -value.toInt()
        } else {
            throw OmuretuException("bad operand value")
        }
        programCounter += PROGRAM_LENGTH
    }
}