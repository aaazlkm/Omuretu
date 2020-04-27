package omuretu.exception

import parser.ast.ASTTree
import java.io.IOException

class TypeException : Exception {
    constructor(e: IOException) : super(e) {}
    constructor(msg: String, astTree: ASTTree? = null) : super("$msg  astTree$astTree") {}
}