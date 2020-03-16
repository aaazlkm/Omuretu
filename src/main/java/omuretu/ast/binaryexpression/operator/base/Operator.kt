package omuretu.ast.binaryexpression.operator.base

import omuretu.Environment

interface Operator

interface LValueOperator : Operator {
    /**
     * TODO
     *
     * @param key
     * @param value
     * @param environment
     * @return
     */
    fun calculate(key: String, value: Any, environment: Environment)
}

interface RValueOperator : Operator {
    /**
     * 計算を行う
     * 失敗したときnullを返す
     *
     * @param left
     * @param right
     * @return
     */
    fun calculate(left: Any, right: Any): Any?
}