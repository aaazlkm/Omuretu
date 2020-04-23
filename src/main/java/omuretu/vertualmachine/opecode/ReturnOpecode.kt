package omuretu.vertualmachine.opecode

import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode

/**
 * return
 *
 * @property virtualMachineStatus
 */
class ReturnOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        fun createByteCode(): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.RETURN.byte)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        programCounter = returnPointer
    }
}