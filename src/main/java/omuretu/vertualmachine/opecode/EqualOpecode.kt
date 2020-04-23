package omuretu.vertualmachine.opecode

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.base.ComputeOpecode

/**
 * equal reg1 reg2
 *
 * @property virtualMachineStatus
 */
class EqualOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue
        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = if (leftValue.toInt() == rightValue.toInt()) OMURETU_TRUE else OMURETU_FALSE
            programCounter += PROGRAM_LENGTH
        } else {
            leftValue?.let {
                registers[leftRegisterIndex] = if (it == rightValue) OMURETU_TRUE else OMURETU_FALSE
            } ?: run {
                registers[leftRegisterIndex] = if (rightValue == null) OMURETU_TRUE else OMURETU_FALSE
            }
        }
    }
}