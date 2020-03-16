package omuretu.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.operator.base.RValueOperator

class EqualOperator : RValueOperator {
    override fun calculate(left: Any, right: Any): Any? {
        return if (left == right) OMURETU_TRUE else OMURETU_FALSE
    }
}