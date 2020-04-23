package util.ex

// TODO 動作確認
fun Short.sliceByByte(): ByteArray {
    return byteArrayOf(
            (this.toInt() ushr 8).toByte(),
            this.toByte()
    )
}