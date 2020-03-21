package omuretu.ast.listeral

import parser.ast.ASTLeaf
import lexer.token.IdToken
import lexer.token.Token
import omuretu.exception.OmuretuException
import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.NestedIdNameLocationMap

class IdNameLiteral(
        override val token: IdToken
) : ASTLeaf(token) {
    companion object Factory: FactoryMethod {
        @JvmStatic
        override fun newInstance(argument: Token): ASTLeaf? {
            return if (argument is IdToken) {
                IdNameLiteral(argument)
            } else {
                null
            }
        }
    }

    val name: String
        get() = token.id

    private var environmentKey: EnvironmentKey? = null

    override fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap) {
        idNameLocationMap.getLocationFromAllMap(name)?.let {
            environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        } ?: run {
            throw OmuretuException("undifined name: $name")
        }
    }

    override fun evaluate(environment: Environment): Any {
        return environmentKey?.let { environment.get(it) } ?: throw OmuretuException("undefined name: ${token.id}", this)
    }

    fun lookupIdNamesForAssign(idNameLocationMap: NestedIdNameLocationMap) {
        val location = idNameLocationMap.putAndReturnLocation(name)
        environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)
    }

    fun evaluateForAssign(environment: Environment, value: Any) {
        environmentKey?.let { environment.put(it, value) } ?: throw OmuretuException("undefined name: ${token.id}", this)
    }
}