package omuretu.vertualmachine.opecode.base

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.OpecodeDefinition

abstract class ComputeOpecode : Opecode() {
    companion object {
        const val REGISTER1_AT = 1
        const val REGISTER2_AT = 2
        const val PROGRAM_LENGTH = 3

        fun createByteCode(operator: String, register1At: Byte, register2At: Byte): ByteArray {
            return mutableListOf<Byte>()
                    .apply {
                        add(getOperatorByteCode(operator))
                        add(register1At)
                        add(register2At)
                    }
                    .toByteArray()
        }

        fun getOperatorByteCode(operator: String): Byte {
            return when (operator) {
                "+" -> OpecodeDefinition.PLUS.byte
                "-" -> OpecodeDefinition.SUB.byte
                "*" -> OpecodeDefinition.MULTI.byte
                "/" -> OpecodeDefinition.DIV.byte
                "%" -> OpecodeDefinition.REM.byte
                "==" -> OpecodeDefinition.EQUAL.byte
                ">" -> OpecodeDefinition.MORE.byte
                "<" -> OpecodeDefinition.LESS.byte
                else -> throw OmuretuException("undefined operator. $operator")
            }
        }
    }

    val leftRegisterIndex: Int
        get() = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER1_AT])
    val rightRegisterIndex: Int
        get() = OmuretuVirtualMachine.decodeRegisterIndex(code[programCounter + REGISTER2_AT])
    val leftValue: Any?
        get() = registers[leftRegisterIndex]
    val rightValue: Any?
        get() = registers[rightRegisterIndex]
}