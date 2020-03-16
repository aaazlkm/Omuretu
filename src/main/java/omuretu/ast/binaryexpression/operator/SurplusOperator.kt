package omuretu.ast.binaryexpression.operator

import omuretu.ast.binaryexpression.operator.base.RValueOperator

class SurplusOperator : RValueOperator {
    override fun calculate(left: Any, right: Any): Any? {
        return if (left is Int && right is Int) {
            left % right
        } else {
            null
        }
    }
}