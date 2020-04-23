package omuretu.vertualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.base.ComputeOpecode

/**
 * rem reg1 reg2
 *
 * @property virtualMachineStatus
 */
class RemOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue

        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = leftValue.toInt() % rightValue.toInt()
            programCounter += PROGRAM_LENGTH
        } else {
            throw OmuretuException("RemOpecode needs int value: left:$leftValue right:$rightValue")
        }
    }
}