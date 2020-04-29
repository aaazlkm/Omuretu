package omuretu.ast.statement

import omuretu.environment.base.VariableEnvironment
import omuretu.environment.NestedIdNameLocationMap
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.GlobalVariableEnvironment
import omuretu.exception.OmuretuException
import parser.ast.ASTList
import parser.ast.ASTTree
import omuretu.model.Class

class ClassStatement(
        private val idNameLiteral: IdNameLiteral,
        private val superClassIdNameLiteral: IdNameLiteral? = null,
        val bodyStmnt: ClassBodyStatement
) : ASTList(if (superClassIdNameLiteral == null) listOf(idNameLiteral, bodyStmnt) else listOf(idNameLiteral, superClassIdNameLiteral, bodyStmnt)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_CLASS = "class"
        const val KEYWORD_EXTENDS = "extends"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            when (argument.size) {
                2 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[1] as? ClassBodyStatement ?: return null
                    return ClassStatement(nameLiteral, null, bodyStmnt)
                }
                3 -> {
                    val nameLiteral = argument[0] as? IdNameLiteral ?: return null
                    val extendLiteral = argument[1] as? IdNameLiteral ?: return null
                    val bodyStmnt = argument[2] as? ClassBodyStatement ?: return null
                    return ClassStatement(nameLiteral, extendLiteral, bodyStmnt)
                }
                else -> {
                    return null
                }
            }
        }
    }

    val name: String
        get() = idNameLiteral.name

    val superClassName: String?
        get() = superClassIdNameLiteral?.name

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        // スーパークラスを持つ場合環境がないとスーパークラスの定義を取得することができないためここでは何もしない
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        val globalEnvironment = variableEnvironment as? GlobalVariableEnvironment
                ?: throw OmuretuException("class can define only in global environment")
        val classMemberLocationMap = NestedIdNameLocationMap(globalEnvironment.idNameLocationMap)
        // クラスボディのメンバー最初にthisを追加
        val thisLocation = classMemberLocationMap.putOnlyThisMapAndReturnLocation("this")
        val classs = Class(this, globalEnvironment, classMemberLocationMap, thisLocation)

        // 継承先のクラスのメンバーをコピー
        classs.superClass?.copyThisMembersTo(classMemberLocationMap)

        // クラスボディ内の変数の位置
        bodyStmnt.lookupIdNamesLocation(classMemberLocationMap)

        // クラス名を登録
        globalEnvironment.putValueByIdName(name, classs)

        return name
    }

    override fun toString(): String {
        return "class name: $idNameLiteral extendLiteral: $superClassIdNameLiteral bodyStmnt: $bodyStmnt"
    }
}