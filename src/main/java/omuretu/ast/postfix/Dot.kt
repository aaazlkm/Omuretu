package omuretu.ast.postfix

import omuretu.Environment
import omuretu.ast.NameLiteral
import omuretu.exception.OmuretuException
import omuretu.model.Class
import omuretu.model.Object
import parser.ast.ASTTree

class Dot(
        private val nameLiteral: NameLiteral
) : Postfix(listOf(nameLiteral)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_DOT = "."
        const val KEYWORD_NEW = "new"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val name = argument.getOrNull(0) as? NameLiteral ?: return null
            return Dot(name)
        }
    }

    val name: String
        get() = nameLiteral.name

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    override fun evaluate(environment: Environment, leftValue: Any): Any {
        when(leftValue) {
            is Class -> {
                if (name == KEYWORD_NEW) {
                    // インスタンス化
                    val objectEnvironment = leftValue.createClassEnvironment()
                    val objectt = Object(objectEnvironment)
                    objectEnvironment.put("this", objectt)
                    return objectt
                } else {
                    throw OmuretuException("bad member access: ", this)
                }
            }
            is Object -> {
                return leftValue.getMember(name) ?: throw OmuretuException("bad member access: ", this)
            }
            else -> {
                throw OmuretuException("bad member access: ", this)
            }
        }
    }
}