package omuretu.ast.postfix

import omuretu.environment.base.VariableEnvironment
import omuretu.ast.listeral.IdNameLiteral
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Class
import omuretu.model.InlineCache
import omuretu.model.Object
import omuretu.typechecker.Type
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

    var objectInlineCache: InlineCache? = null

    override fun checkType(typeEnvironment: TypeEnvironment, leftType: Type): Type {
        // TODO クラスのプロパティの型を見るようにする
        return Type.Defined.Any
    }

    override fun evaluate(variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return when (leftValue) {
            is Class -> evaluateWhenCalss(leftValue)
            is Object -> evaluateWhenObject(leftValue)
            else -> throw OmuretuException("bad member access: ", this)
        }
    }

    private fun evaluateWhenCalss(classs: Class): Any {
        if (name == KEYWORD_NEW) {
            // インスタンス化
            val objectt = Object(classs)
            val objectEnvironment = classs.createClassEnvironment(objectt)
            objectt.variableEnvironment = objectEnvironment
            return objectt
        } else {
            throw OmuretuException("bad member access: ", this)
        }
    }

    private fun evaluateWhenObject(objectt: Object): Any {
        val inlineCache = objectInlineCache
        return if (objectt.classs == inlineCache?.classs) {
            objectt.getMember(inlineCache.location) ?: throw OmuretuException("bad member access: ", this)
        } else {
            val memberLocaiton = objectt.getMemberLocationOf(name) ?: throw OmuretuException("bad member access: ", this)
            objectInlineCache = InlineCache(objectt.classs, memberLocaiton)
            objectt.getMember(memberLocaiton) ?: throw OmuretuException("bad member access: ", this)
        }
    }
}