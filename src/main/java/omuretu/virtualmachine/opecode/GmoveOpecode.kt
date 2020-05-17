package omuretu.virtualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.OpecodeDefinition
import omuretu.virtualmachine.opecode.base.Opecode
import util.ex.sliceByByte

/**
 * TODO 動作確認
 * gmove src dest (src,dest = (reg, int16, int16) || (int16, int16, reg))
 *
 * @property virtualMachineStatus
 */
class GmoveOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        // when src is register
        const val INT_START_WHEN_SRC_REGISTER = 2
        const val INT_END_WHEN_SRC_REGISTER = 3

        // when src is not register
        const val INT_START_WHEN_SRC_NOT_REGISTER = 1
        const val INT_END_WHEN_SRC_NOT_REGISTER = 2
        const val REGISTER_AT_WHEN_SRC_NOT_REGISTER = 3

        const val PROGRAM_LENGTH = 4

        fun createByteCode(registerAt: Byte, value: Short): ByteArray {

            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.GMOVE.byte)
                        add(registerAt)
                        addAll(value.sliceByByte().toList())
                    }
                    .toByteArray()
        }

        fun createByteCode(value: Short, registerAt: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.GMOVE.byte)
                        addAll(value.sliceByByte().toList())
                        add(registerAt)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val src = code[programCounter + 1]
        if (OmuretuVirtualMachine.isRegister(src)) {
            val array = code.slice((programCounter + INT_START_WHEN_SRC_REGISTER)..(programCounter + INT_END_WHEN_SRC_REGISTER)).toByteArray()
            val dest = OmuretuVirtualMachine.readShort(array) ?: throw OmuretuException("failed to read short: $array")
            val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(src)
            heapMemory.write(dest, registers[registerIndex])
        } else {
            val array = code.slice((programCounter + INT_START_WHEN_SRC_NOT_REGISTER)..(programCounter + INT_END_WHEN_SRC_NOT_REGISTER)).toByteArray()
            val srcShort = OmuretuVirtualMachine.readShort(array) ?: throw OmuretuException("failed to read short: $array")
            val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT_WHEN_SRC_NOT_REGISTER])
            registers[registerIndex] = heapMemory.read(srcShort)
        }
        programCounter += PROGRAM_LENGTH
    }
}
