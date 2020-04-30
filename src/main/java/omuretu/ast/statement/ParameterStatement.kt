package omuretu.ast.statement

import omuretu.ast.TypeTag
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import parser.ast.ASTList
import parser.ast.ASTTree

class ParameterStatement(
        private val idNameLiteral: IdNameLiteral,
        private val typeTag: TypeTag
) : ASTList(listOf(idNameLiteral, typeTag)) {
    companion object Factory : FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 2) return null
            val idNameLiteral = argument[0] as? IdNameLiteral ?: return null
            val typeTag = argument[1] as? TypeTag ?: return null
            return ParameterStatement(idNameLiteral, typeTag)
        }
    }

    val name: String
        get() = idNameLiteral.name

    val type: Type.Defined
        get() = typeTag.type as Type.Defined // パラメータ型は事前に決まっている

    override fun toString() = "$idNameLiteral $typeTag"

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        return typeTag.type ?: throw OmuretuException("undefined type name:", this)
    }
}