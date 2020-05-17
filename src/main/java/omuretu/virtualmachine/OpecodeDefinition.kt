package omuretu.virtualmachine

import omuretu.virtualmachine.opecode.BConstOpecode
import omuretu.virtualmachine.opecode.CallOpecode
import omuretu.virtualmachine.opecode.DivOpecode
import omuretu.virtualmachine.opecode.EqualOpecode
import omuretu.virtualmachine.opecode.GmoveOpecode
import omuretu.virtualmachine.opecode.GotoOpecode
import omuretu.virtualmachine.opecode.IConstOpecode
import omuretu.virtualmachine.opecode.IfZeroOpecode
import omuretu.virtualmachine.opecode.LessOpecode
import omuretu.virtualmachine.opecode.MoreOpecode
import omuretu.virtualmachine.opecode.MoveOpecode
import omuretu.virtualmachine.opecode.MulOpecode
import omuretu.virtualmachine.opecode.NegOpecode
import omuretu.virtualmachine.opecode.PlusOpecode
import omuretu.virtualmachine.opecode.RemOpecode
import omuretu.virtualmachine.opecode.RestoreOpecode
import omuretu.virtualmachine.opecode.ReturnOpecode
import omuretu.virtualmachine.opecode.SConstOpecode
import omuretu.virtualmachine.opecode.SaveOpecode
import omuretu.virtualmachine.opecode.SubOpecode
import omuretu.virtualmachine.opecode.base.Opecode

enum class OpecodeDefinition {
    /** load an integer */
    ICONST,
    /** load an 8bit (1byte) integer */
    BCONST,
    /** load a character string */
    SCONST,
    /** move a value */
    MOVE,
    /** move a value (global variable) */
    GMOVE,
    /** branch if false */
    IFZERO,
    /** always branch */
    GOTO,
    /** call a function */
    CALL,
    /** return */
    RETURN,
    /** save all registers */
    SAVE,
    /** restore all registers */
    RESTORE,
    /** arithmetic negation */
    NEG,
    /** add */
    PLUS,
    /** subtract */
    SUB,
    /**  multiply */
    MULTI,
    /** divide */
    DIV,
    /** remainder */
    REM,
    /** equal */
    EQUAL,
    /** more than */
    MORE,
    /** less than */
    LESS;

    companion object {
        fun form(byte: Byte): OpecodeDefinition? {
            return values().firstOrNull { it.byte == byte }
        }
    }

    val byte: Byte
        get() = when (this) {
            ICONST -> 1
            BCONST -> 2
            SCONST -> 3
            MOVE -> 4
            GMOVE -> 5
            IFZERO -> 6
            GOTO -> 7
            CALL -> 8
            RETURN -> 9
            SAVE -> 10
            RESTORE -> 11
            NEG -> 12
            PLUS -> 13
            SUB -> 14
            MULTI -> 15
            DIV -> 16
            REM -> 17
            EQUAL -> 18
            MORE -> 19
            LESS -> 20
        }

    fun createRunner(virtualMachineStatus: OmuretuVirtualMachine.Status): Opecode {
        return when (this) {
            ICONST -> IConstOpecode(virtualMachineStatus)
            BCONST -> BConstOpecode(virtualMachineStatus)
            SCONST -> SConstOpecode(virtualMachineStatus)
            MOVE -> MoveOpecode(virtualMachineStatus)
            GMOVE -> GmoveOpecode(virtualMachineStatus)
            IFZERO -> IfZeroOpecode(virtualMachineStatus)
            GOTO -> GotoOpecode(virtualMachineStatus)
            CALL -> CallOpecode(virtualMachineStatus)
            RETURN -> ReturnOpecode(virtualMachineStatus)
            SAVE -> SaveOpecode(virtualMachineStatus)
            RESTORE -> RestoreOpecode(virtualMachineStatus)
            NEG -> NegOpecode(virtualMachineStatus)
            PLUS -> PlusOpecode(virtualMachineStatus)
            SUB -> SubOpecode(virtualMachineStatus)
            MULTI -> MulOpecode(virtualMachineStatus)
            DIV -> DivOpecode(virtualMachineStatus)
            REM -> RemOpecode(virtualMachineStatus)
            EQUAL -> EqualOpecode(virtualMachineStatus)
            MORE -> MoreOpecode(virtualMachineStatus)
            LESS -> LessOpecode(virtualMachineStatus)
        }
    }
}
