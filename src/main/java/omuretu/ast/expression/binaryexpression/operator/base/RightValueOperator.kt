package omuretu.ast.expression.binaryexpression.operator.base

import omuretu.environment.base.VariableEnvironment
import omuretu.visitor.EvaluateVisitor

/**
 * 右辺値オペレーター
 */
interface RightValueOperator : Operator {
    /**
     * TODO
     *
     * @param key
     * @param value
     * @param environment
     * @return
     */
    fun calculate(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any
}
