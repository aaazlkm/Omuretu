package omuretu.operator

import omuretu.exception.OmuretuException
import omuretu.operator.base.RValueOperator

class MinusOperator : RValueOperator {
    override fun calculate(left: Any, right: Any): Any? {
        return if (left is Int && right is Int) {
            left - right
        } else {
            null
        }
    }
}