package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode
import util.ex.sliceByByte

/**
 * goto int16
 *
 * @property virtualMachineStatus
 */
class GotoOpecode (
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val SHORT_START = 1
        const val SHORT_END = 2

        fun createByteCode(value: Short): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.GOTO.byte)
                        addAll(value.sliceByByte().toList())
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val offsetArray = code.slice((programCounter + SHORT_START)..(programCounter + SHORT_END)).toByteArray()
        programCounter += OmuretuVirtualMachine.readShort(offsetArray) ?: throw OmuretuException("failed to read short: $offsetArray")
    }
}