package omuretu.ast.postfix

import omuretu.environment.Environment
import omuretu.ast.listeral.IdNameLiteral
import omuretu.exception.OmuretuException
import omuretu.model.Class
import omuretu.model.Object
import parser.ast.ASTTree

class DotPostfix(
        private val idNameLiteral: IdNameLiteral
) : Postfix(listOf(idNameLiteral)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_DOT = "."
        const val KEYWORD_NEW = "new"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            val name = argument.getOrNull(0) as? IdNameLiteral ?: return null
            return DotPostfix(name)
        }
    }

    val name: String
        get() = idNameLiteral.name

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
//                    objectEnvironment.put("this", objectt) // TODO thisも使用できるようにする
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