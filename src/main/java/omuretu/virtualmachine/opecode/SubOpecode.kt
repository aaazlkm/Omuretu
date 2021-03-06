package omuretu.virtualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.opecode.base.ComputeOpecode

/**
 * sub reg1 reg2
 *
 * @property virtualMachineStatus
 */
class SubOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue

        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = leftValue.toInt() - rightValue.toInt()
            programCounter += PROGRAM_LENGTH
        } else {
            throw OmuretuException("SubOpecode needs int value: left:$leftValue right:$rightValue")
        }
    }
}
