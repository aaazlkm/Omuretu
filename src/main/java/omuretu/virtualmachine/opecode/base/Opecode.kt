package omuretu.virtualmachine.opecode.base

import omuretu.virtualmachine.HeapMemory
import omuretu.virtualmachine.OmuretuVirtualMachine

abstract class Opecode {
    /**
     * virtual machine の状態
     * これを用いてそれぞれのopecodeの実行を行う
     */
    abstract val virtualMachineStatus: OmuretuVirtualMachine.Status

    val code: ByteArray
        get() = virtualMachineStatus.code

    var stack: Array<Any?>
        get() = virtualMachineStatus.stack
        set(value) {
            virtualMachineStatus.stack = value
        }

    var heapMemory: HeapMemory
        get() = virtualMachineStatus.heapMemory
        set(value) {
            virtualMachineStatus.heapMemory = value
        }

    var strings: Array<String>
        get() = virtualMachineStatus.strings
        set(value) {
            virtualMachineStatus.strings = value
        }

    var programCounter: Int
        get() = virtualMachineStatus.programCounter
        set(value) {
            virtualMachineStatus.programCounter = value
        }

    var framePointer: Int
        get() = virtualMachineStatus.framePointer
        set(value) {
            virtualMachineStatus.framePointer = value
        }

    var stackPointer: Int
        get() = virtualMachineStatus.stackPointer
        set(value) {
            virtualMachineStatus.stackPointer = value
        }

    var returnPointer: Int
        get() = virtualMachineStatus.returnPointer
        set(value) {
            virtualMachineStatus.returnPointer = value
        }

    var registers: Array<Any?>
        get() = virtualMachineStatus.registers
        set(value) {
            virtualMachineStatus.registers = value
        }

    abstract fun run()
}
