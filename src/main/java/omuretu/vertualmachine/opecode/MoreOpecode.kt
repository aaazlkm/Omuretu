package omuretu.vertualmachine.opecode

import omuretu.OMURETU_TRUE
import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.base.ComputeOpecode

/**
 * more reg1 reg2
 *
 * @property virtualMachineStatus
 */
class MoreOpecode(
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue

        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = if (leftValue.toInt() > rightValue.toInt()) OMURETU_TRUE else OMURETU_TRUE
            programCounter += PROGRAM_LENGTH
        } else {
            throw OmuretuException("MoreOpecode needs int value: left:$leftValue right:$rightValue")
        }
    }
}