package omuretu.ast.expression.binaryexpression.operator.base

import omuretu.environment.base.VariableEnvironment
import omuretu.model.InlineCache
import omuretu.visitor.EvaluateVisitor

/**
 * 左辺値オペレーター
 */
interface LeftValueOperator : Operator {
    /**
     * TODO
     *
     * @param key
     * @param value
     * @param environment
     * @return
     */
    fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment, inlineCache: InlineCache? = null, doOnSaveInlineCache: ((InlineCache) -> Unit)? = null): Any
}
