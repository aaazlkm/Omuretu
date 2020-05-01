package util.ex

fun Short.sliceByByte(): ByteArray {
    return byteArrayOf(
            (this.toInt() ushr 8).toByte(),
            this.toByte()
    )
}