package exception

import token.Token
import java.io.IOException

class ParseException : Exception {
    constructor(e: IOException) : super(e) {}
    constructor(msg: String) : super(msg) {}
}
