package omuretu.ast.expression.binaryexpression.operator.base

import omuretu.environment.base.VariableEnvironment
import omuretu.model.InlineCache
import omuretu.visitor.EvaluateVisitor
import parser.ast.ASTTree

/**
 * オペレーター
 *
 * 動的ポリモーフィズムにしたため、inlineCacheによる実行速度改善のメリットよりも、動的ポリモーフィズムに変更したことによるデメリットの方が大きく実行速度が遅くなっている
 * 今回は速度に関しては求めていないので、勉強のためにinlineCacheを用いた実装を採用する
 *
 */
interface Operator {
    val leftTree: ASTTree

    val rightTree: ASTTree

    val evaluateVisitor: EvaluateVisitor

    val variableEnvironment: VariableEnvironment
}

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
    fun calculate(inlineCache: InlineCache? = null, doOnSaveInlineCache: ((InlineCache) -> Unit)? = null): Any
}

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
    fun calculate(): Any
}