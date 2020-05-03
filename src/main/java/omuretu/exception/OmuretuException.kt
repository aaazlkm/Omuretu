package omuretu.exception

import java.io.IOException
import parser.ast.ASTTree

class OmuretuException : Exception {
    constructor(e: IOException) : super(e)
    constructor(msg: String, astTree: ASTTree? = null) : super("$msg astTree: $astTree")
}
