package omuretu.ast.binaryexpression.operator

import omuretu.OMURETU_FALSE
import omuretu.OMURETU_TRUE
import omuretu.ast.binaryexpression.operator.base.RValueOperator

class EqualOperator : RValueOperator {
    override fun calculate(left: Any, right: Any): Any? {
        return if (left == right) OMURETU_TRUE else OMURETU_FALSE
    }
}