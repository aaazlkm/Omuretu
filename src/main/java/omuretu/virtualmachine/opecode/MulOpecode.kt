package omuretu.virtualmachine.opecode

import omuretu.exception.OmuretuException
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.opecode.base.ComputeOpecode

/**
 * mul reg1 reg2
 *
 * @property virtualMachineStatus
 */
class MulOpecode(
    override val virtualMachineStatus: OmuretuVirtualMachine.Status
) : ComputeOpecode() {
    override fun run() {
        val leftValue = (leftValue as? Number)?.toInt() ?: throw OmuretuException("MulOpecode needs int value: left:$leftValue right:$rightValue")
        val rightValue = (rightValue as? Number)?.toInt() ?: throw OmuretuException("MulOpecode needs int value: left:$leftValue right:$rightValue")

        registers[leftRegisterIndex] = leftValue * rightValue
        programCounter += PROGRAM_LENGTH
    }
}
