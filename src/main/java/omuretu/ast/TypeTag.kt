package omuretu.ast

import omuretu.ast.listeral.IdNameLiteral
import omuretu.typechecker.Type
import parser.ast.ASTList
import parser.ast.ASTTree

class TypeTag(
        private val typeName: IdNameLiteral? = null
) : ASTList(typeName?.let { listOf(it) } ?: listOf()) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_COLON = ":"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size !in 0..1) return null
            return when(argument.size) {
                0 -> TypeTag()
                1 -> {
                    val typeName = argument[0] as? IdNameLiteral ?: return null
                    return TypeTag(typeName)
                }
                else -> null
            }
        }
    }

    private val name: String
        get() = typeName?.name ?: Type.NeedInference.NAME

    val type: Type?
        get() = Type.from(name)

    override fun toString() = "$KEYWORD_COLON $typeName"
}