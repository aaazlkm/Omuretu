package omuretu.virtualmachine.opecode

import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode

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
