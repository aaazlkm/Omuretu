package omuretu.ast.statement

import omuretu.Environment
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import parser.ast.ASTList
import parser.ast.ASTTree

class ClassBodyStmnt(
        val members: List<ASTTree>
) : ASTList(members) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACES_START = "{"
        const val KEYWORD_BRACES_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ClassBodyStmnt(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        members.forEach { it.evaluate(environment) }
        // `environment`にmemberを追加するだけでいいので、返り値はなんでもいい
        return OMURETU_DEFAULT_RETURN_VALUE
    }

    override fun toString(): String {
        return "(classbody: $members)"
    }
}