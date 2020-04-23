package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode
import util.ex.sliceByByte

/**
 * iconst int32 reg
 *
 * @property virtualMachineStatus
 */
class IConstOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val INT_START = 1
        const val INT_END = 4
        const val REGISTER_AT = 5
        const val PROGRAM_LENGTH = 6

        fun createByteCode(value: Int, registerAt: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.ICONST.byte)
                        addAll(value.sliceByByte().toList())
                        add(registerAt)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        val array = code.slice((programCounter + INT_START)..(programCounter + INT_END)).toByteArray()
        registers[registerIndex] = OmuretuVirtualMachine.readInt(array) ?: throw OmuretuException("failed to read int: $array")
        programCounter += PROGRAM_LENGTH
    }
}