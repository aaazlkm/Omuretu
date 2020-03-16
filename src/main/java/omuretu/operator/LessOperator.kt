package omuretu.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.operator.base.RValueOperator

class LessOperator: RValueOperator {
    override fun calculate(left: Any, right: Any): Any? {
        return if (left is Int && right is Int) {
            if (left < right) OMURETU_TRUE else OMURETU_FALSE
        } else {
            null
        }
    }
}