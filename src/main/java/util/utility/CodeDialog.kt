package util.utility

import java.io.*
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

class CodeDialog : Reader() {
    private var buffer: String? = null
    private var pos = 0

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        if (buffer == null) {
            val `in` = showDialog()
            if (`in` == null)
                return -1
            else {
                print(`in`)
                buffer = `in` + "\n"
                pos = 0
            }
        }

        var size = 0
        val length = buffer!!.length
        while (pos < length && size < len)
            cbuf[off + size++] = buffer!![pos++]
        if (pos == length)
            buffer = null
        return size
    }

     fun print(s: String) {
        println(s)
    }

    @Throws(IOException::class)
    override fun close() {
    }

     fun showDialog(): String? {
        val area = JTextArea(20, 40)
        val pane = JScrollPane(area)
        val result = JOptionPane.showOptionDialog(null, pane, "Input",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null)
        return if (result == JOptionPane.OK_OPTION)
            area.text
        else
            null
    }

    companion object {
        @Throws(FileNotFoundException::class)
        fun file(): Reader {
            val chooser = JFileChooser()
            return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                BufferedReader(FileReader(chooser.selectedFile))
            else
                throw FileNotFoundException("no file specified")
        }
    }
}