package parser.ast

open class ASTList() : ASTTree {
    constructor(astTrees: List<ASTTree>) : this() {
        children = astTrees
    }

    open lateinit var children: List<ASTTree>

    val numberOfChildren: Int
        get() = children.size
}

// TODO 命名
abstract class AstListWithChildren(astTrees: List<ASTTree>) : ASTList(astTrees) {

    abstract fun make(astTrees: List<ASTTree> ): ASTTree
}