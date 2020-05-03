package omuretu.virtualmachine.opecode

import omuretu.OMURETU_FALSE
import omuretu.exception.OmuretuException
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode
import util.ex.sliceByByte

/**
 * ifzero reg int16
 *
 * @property virtualMachineStatus
 */
class IfZeroOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val REGISTER_AT = 1
        const val SHORT_START = 2
        const val SHORT_END = 3
        const val PROGRAM_LENGTH_WHEN_TRUE = 4

        fun createByteCode(registerAt: Byte, value: Short): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.IFZERO.byte)
                        add(registerAt)
                        addAll(value.sliceByByte().toList())
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        val value = registers[registerIndex]
        programCounter +=
                if (value is Int && value == OMURETU_FALSE) {
                    val array = code.slice((programCounter + SHORT_START)..(programCounter + SHORT_END)).toByteArray()
                    OmuretuVirtualMachine.readShort(array) ?: throw OmuretuException("failed to read short: $array")
                } else {
                    PROGRAM_LENGTH_WHEN_TRUE
                }
    }
}
