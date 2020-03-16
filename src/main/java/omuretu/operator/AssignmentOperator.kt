package omuretu.operator

import omuretu.operator.base.LValueOperator
import parser.Environment

class AssignmentOperator: LValueOperator {
    override fun calculate(key: String, value: Any, environment: Environment) {
        environment.put(key, value)
    }
}