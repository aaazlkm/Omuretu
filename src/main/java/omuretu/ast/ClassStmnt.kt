package omuretu.ast

import omuretu.Environment
import parser.ast.ASTList
import parser.ast.ASTTree
import omuretu.model.Class

class ClassStmnt(
        private val nameLiteral: NameLiteral,
        private val superClassNameLiteral: NameLiteral? = null,
        val bodyStmnt: ClassBodyStmnt
) : ASTList(if(superClassNameLiteral == null) listOf(nameLiteral, bodyStmnt) else listOf(nameLiteral, superClassNameLiteral, bodyStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLASS = "class"
        const val KEYWORD_EXTENDS = "extends"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
             when(argument.size) {
                2 -> {
                    val nameLiteral = argument[0] as? NameLiteral ?: return null
                    val bodyStmnt = argument[1] as? ClassBodyStmnt ?: return null
                    return ClassStmnt(nameLiteral, null, bodyStmnt)
                }
                3 -> {
                    val nameLiteral = argument[0] as? NameLiteral ?: return null
                    val extendLiteral = argument[1] as? NameLiteral ?: return null
                    val bodyStmnt = argument[2] as? ClassBodyStmnt ?: return null
                    return ClassStmnt(nameLiteral, extendLiteral, bodyStmnt)
                }
                else -> {
                    return null
                }
            }
        }
    }

    val name: String
        get()  = nameLiteral.name

    val superClassName: String?
        get() = superClassNameLiteral?.name

    override fun evaluate(environment: Environment): Any {
        val classs = Class(this, environment)
        environment.put(name, classs)
        return name
    }

    override fun toString(): String {
        return "class name: $nameLiteral extendLiteral: $superClassNameLiteral bodyStmnt: $bodyStmnt"
    }
}