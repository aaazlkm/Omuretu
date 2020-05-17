package omuretu.virtualmachine

class ByteCodeStore(
    codeSize: Int = OmuretuVirtualMachine.DEFAULT_CODE_SIZE,
    stringSize: Int = OmuretuVirtualMachine.DEFAULT_STRINGS_SIZE
) {
    val byteCode = ByteArray(codeSize) { -100 }
    val strings = Array(stringSize) { "" }

    var stackFrameSize = 0

    var registerPosition = 0
    var codePosition = 0

    fun addByteCode(byteCode: Byte) {
        this.byteCode[codePosition++] = byteCode
    }

    fun setByteCode(position: Int, byteCode: Byte) {
        this.byteCode[position] = byteCode
    }

    fun setRegisterAt(position: Int) {
        registerPosition = position
    }

    fun nextRegister(): Int {
        return registerPosition++
    }

    fun prevRegister(): Int {
        return if (registerPosition == 0) {
            0
        } else {
            --registerPosition
        }
    }
}
