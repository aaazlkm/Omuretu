package omuretu.ast.binaryexpression.operator

import omuretu.ast.binaryexpression.operator.base.LValueOperator
import omuretu.Environment

class AssignmentOperator: LValueOperator {
    override fun calculate(key: String, value: Any, environment: Environment) {
        environment.put(key, value)
    }
}