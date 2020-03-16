package omuretu.ast.postfix

import omuretu.Environment
import omuretu.NestedEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Function
import parser.ast.ASTTree

class Argument(
        private val astTrees: List<ASTTree>
) : Postfix(astTrees) {
    companion object Factory : FactoryMethod {
        const val KEYWORD_ARGUMENT_BREAK = ","

        @JvmStatic
        override fun newInstance(argument: List<ASTTree>): ASTTree? {
            return Argument(argument)
        }
    }

    override fun evaluate(environment: Environment): Any {
        throw OmuretuException("must be called `evaluate(environment: Environment, value: Any)` instead of this method", this)
    }

    /**
     * environmentは大域変数だったり、別の関数の局所変数になる
     * この関数ないで使用できる変数の情報が入っている
     *
     * @param environment
     * @param value
     * @return
     */
    override fun evaluate(environment: Environment, value: Any): Any {
        val function = value as? Function ?: throw OmuretuException("bad function", this)
        if (astTrees.size != function.parameters.parameterNames.size) throw OmuretuException("bad number odf argument", this)
        val nestedEnvironment = NestedEnvironment(function.environment as? NestedEnvironment)
        // パラメータの値をenvironmentに追加
        function.parameters.parameterNames.forEachIndexed { index, parameterName ->
            nestedEnvironment.putInThisEnvironment(parameterName, astTrees[index].evaluate(environment))
        }
        return function.blockStmnt.evaluate(nestedEnvironment)
    }
}