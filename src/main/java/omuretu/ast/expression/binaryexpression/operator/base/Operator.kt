package omuretu.ast.expression.binaryexpression.operator.base

import omuretu.environment.base.TypeEnvironment
import omuretu.typechecker.Type
import omuretu.visitor.CheckTypeVisitor
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

    fun checkType(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type
}
