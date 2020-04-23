package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode
import util.ex.sliceByByte

/**
 * sconst int16 reg
 *
 * @property virtualMachineStatus
 */
class SConstOpecode  (
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val SHORT_START = 1
        const val SHORT_END = 2
        const val REGISTER_AT = 3
        const val PROGRAM_LENGTH = 4

        fun createByteCode(value: Short, registerAt: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.SCONST.byte)
                        addAll(value.sliceByByte().toList())
                        add(registerAt)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        val array = code.slice((programCounter + SHORT_START)..(programCounter + SHORT_END)).toByteArray()
        val stringIndex = OmuretuVirtualMachine.readShort(array) ?: throw OmuretuException("failed to read short: $array")
        registers[registerIndex] = strings[stringIndex]
        programCounter += PROGRAM_LENGTH
    }
}