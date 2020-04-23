package omuretu.vertualmachine.opecode

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.exception.OmuretuException
import omuretu.vertualmachine.OmuretuVirtualMachine
import omuretu.vertualmachine.opecode.base.ComputeOpecode

/**
 * less reg1 reg2
 *
 * @property virtualMachineStatus
 */
class LessOpecode (
        override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = leftValue
        val rightValue = rightValue

        if (leftValue is Number && rightValue is Number) {
            registers[leftRegisterIndex] = if (leftValue.toInt() < rightValue.toInt()) OMURETU_TRUE else OMURETU_FALSE
            programCounter += PROGRAM_LENGTH
        } else {
            throw OmuretuException("LessOpecode needs int value: left:$leftValue right:$rightValue")
        }
    }
}