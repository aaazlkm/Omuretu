package omuretu.ast.statement

import omuretu.NestedIdNameLocationMap
import omuretu.ast.TypeTag
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.VariableEnvironment
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper
import parser.ast.ASTList
import parser.ast.ASTTree

class VarStmnt(
        private val idNameLiteral: IdNameLiteral,
        private val typeTag: TypeTag,
        private val initializer: ASTTree
) : ASTList(listOf(idNameLiteral, typeTag, initializer)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_VAR = "var"
        const val KEYWORD_EQUAL = "="

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 3) return null
            val idNameLiteral = argument[0] as? IdNameLiteral ?: return null
            val typeTag = argument[1] as? TypeTag ?: return null
            val initializer = argument[2] as? ASTTree ?: return null
            return VarStmnt(idNameLiteral, typeTag, initializer)
        }
    }

    val name: String
        get() = idNameLiteral.name

    var environmentKey: EnvironmentKey? = null

    override fun toString() = "$KEYWORD_VAR $idNameLiteral $typeTag $KEYWORD_EQUAL $initializer"

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(name).let {
            environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
        initializer.lookupIdNamesLocation(idNameLocationMap)
    }

    override fun checkType(typeEnvironment: TypeEnvironment): Type {
        val environmentKey = environmentKey ?: throw OmuretuException("undefined", this)
        if (typeEnvironment.get(environmentKey) != null) throw OmuretuException("duplicate variable ${idNameLiteral.name}", this)
        val varType = typeTag.type ?: throw OmuretuException("undefined type: ${typeTag.type}", this)
        val initializerType = initializer.checkType(typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(varType, initializerType, this, typeEnvironment)
        typeEnvironment.put(environmentKey, initializerType)
        return varType
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        val environmentKey = environmentKey ?: throw OmuretuException("undefined", this)
        val value = initializer.evaluate(variableEnvironment)
        variableEnvironment.put(environmentKey, value)
        return value
    }
}