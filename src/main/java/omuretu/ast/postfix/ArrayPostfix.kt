package omuretu.ast.postfix

import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import parser.ast.ASTTree

class ArrayPostfix(
        val index: ASTTree
) : Postfix(listOf(index)) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_BRACKETS_START = "["
        const val KEYWORD_BRACKETS_END = "]"

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            if (argument.size != 1) return null
            return ArrayPostfix(argument.first())
        }
    }

    override fun checkType(typeEnvironment: TypeEnvironment, leftType: Type): Type {
        // TODO 配列型を用意する
        return Type.Defined.Any
    }

    override fun evaluate(variableEnvironment: VariableEnvironment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    override fun evaluate(variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        val list = (leftValue as? MutableList<*>)?.mapNotNull { it } ?: throw OmuretuException("bad array access")
        val index = index.evaluate(variableEnvironment) as? Int ?: throw OmuretuException("bad array access")
        return list[index]
    }
}