package omuretu.ast.statement

import omuretu.environment.NestedIdNameLocationMap
import omuretu.environment.base.VariableEnvironment
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import parser.ast.ASTList
import parser.ast.ASTTree

class ClassBodyStatement(
        val members: List<ASTTree>
) : ASTList(members) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACES_START = "{"
        const val KEYWORD_BRACES_END = "}"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return ClassBodyStatement(argument)
        }
    }

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        members.forEach { it.lookupIdNamesLocation(idNameLocationMap) }
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        members.forEach { it.evaluate(variableEnvironment) }
        // `environment`にmemberを追加するだけでいいので、返り値はなんでもいい
        return OMURETU_DEFAULT_RETURN_VALUE
    }

    override fun toString(): String {
        return "(classbody: $members)"
    }
}