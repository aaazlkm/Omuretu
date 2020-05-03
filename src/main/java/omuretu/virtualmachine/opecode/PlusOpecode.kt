package omuretu.virtualmachine.opecode

import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.opecode.base.ComputeOpecode

/**
 * add reg1 reg2
 *
 * @property virtualMachineStatus
 */
class PlusOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue

        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = leftValue.toInt() + rightValue.toInt()
            programCounter += PROGRAM_LENGTH
        } else {
            registers[leftRegisterIndex] = leftValue.toString() + rightValue.toString()
        }
    }
}
