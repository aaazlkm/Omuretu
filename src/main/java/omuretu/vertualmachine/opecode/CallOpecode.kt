package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.model.Function
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition
import omuretu.vertualmachine.opecode.base.Opecode

/**
 * call reg int8
 *
 * @property virtualMachineStatus
 */
class CallOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : Opecode() {
    companion object {
        const val REGISTER_AT = 1
        const val NUMBER_OF_ARGUMENTS_AT = 2
        const val PROGRAM_LENGTH = 3

        fun createByteCode(registerAt: Byte, numberOfArguments: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(OpecodeDefinition.CALL.byte)
                        add(registerAt)
                        add(numberOfArguments)
                    }
                    .toByteArray()
        }
    }

    override fun run() {
        val registerIndex = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER_AT])
        val value = registers[registerIndex]
        val numberOfArguments = code[programCounter + NUMBER_OF_ARGUMENTS_AT].toInt()
        when (value) {
            is Function.OmuretuFunction -> {
                if (value.numberOfParameter != numberOfArguments) throw OmuretuException("does not match number of parameters")
                returnPointer = programCounter + PROGRAM_LENGTH
                programCounter = value.entry
            }
            is Function.NativeFunction -> {
                if (value.numberOfParameter != numberOfArguments) throw OmuretuException("does not match number of parameters")
                val args = arrayOfNulls<Any>(numberOfArguments)
                for (i in 0 until numberOfArguments) {
                    args[i] = stack[stackPointer + i]
                }
                stack[stackPointer] = value.invoke(args)
                programCounter += PROGRAM_LENGTH
            }
            else -> {
                throw OmuretuException("bad function call")
            }
        }
    }
}
