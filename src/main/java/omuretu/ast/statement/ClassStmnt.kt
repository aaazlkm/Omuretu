package omuretu.ast.statement

import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap
import omuretu.ast.listeral.IdNameLiteral
import omuretu.exception.OmuretuException
import parser.ast.ASTList
import parser.ast.ASTTree
import omuretu.model.Class

class ClassStmnt(
        private val idNameLiteral: IdNameLiteral,
        private val superClassIdNameLiteral: IdNameLiteral? = null,
        val bodyStmnt: ClassBodyStmnt
) : ASTList(if(superClassIdNameLiteral == null) listOf(idNameLiteral, bodyStmnt) else listOf(idNameLiteral, superClassIdNameLiteral, bodyStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLASS = "class"
        const val KEYWORD_EXTENDS = "extends"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
             when(argument.size) {
                2 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[1] as? ClassBodyStmnt ?: return null
                    return ClassStmnt(nameLiteral, null, bodyStmnt)
                }
                3 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val extendLiteral = argument[1] as? IdNameLiteral ?: return null
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
        get()  = idNameLiteral.name

    val superClassName: String?
        get() = superClassIdNameLiteral?.name

    var environmentKey: EnvironmentKey? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        val location = idNameLocationMap.putAndReturnLocation(idNameLiteral.name)
        environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)
    }

    override fun evaluate(environment: Environment): Any {
        val environmentKey = environmentKey ?: throw OmuretuException("donot defined class name ${idNameLiteral.name}")
        val classs = Class(this, environment)
        environment.put(environmentKey, classs)
        return name
    }

    override fun toString(): String {
        return "class name: $idNameLiteral extendLiteral: $superClassIdNameLiteral bodyStmnt: $bodyStmnt"
    }
}