package util.ex

fun Int.sliceByByte(): ByteArray {
    return byteArrayOf(
            (this ushr 24).toByte(),
            (this ushr 16).toByte(),
            (this ushr 8).toByte(),
            this.toByte()
    )
}
