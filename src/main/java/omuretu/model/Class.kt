package omuretu.model

import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.IdNameLocationMap
import omuretu.environment.Location
import omuretu.environment.VariableEnvironmentImpl
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.VariableEnvironment
import omuretu.visitor.EvaluateVisitor

data class Class(
    val classStatement: ClassStatement,
    private val globalEnvironment: GlobalVariableEnvironment,
    private val classMemberLocationMap: IdNameLocationMap,
    private val thisLocation: Location
) {
    val body: ClassBodyStatement
        get() = classStatement.bodyStatement

    override fun toString(): String {
        return "class: $classStatement"
    }

    fun copyThisMembersTo(classMemberLocationMap: IdNameLocationMap) {
        classMemberLocationMap.copyFrom(this.classMemberLocationMap)
    }

    fun getMemberLocationOf(idName: String): Location? {
        return classMemberLocationMap.getLocationFromOnlyThisMap(idName)
    }

    //region create class environment

    fun createClassEnvironment(objectt: Object, evaluateVisitor: EvaluateVisitor): VariableEnvironmentImpl {
        val environment = VariableEnvironmentImpl(classMemberLocationMap.idNamesSize, globalEnvironment as? VariableEnvironmentImpl)
        addThisKeyWordToEnvironment(environment, objectt)
        crateSuperClassEnvironment(this, evaluateVisitor, environment)
        return environment
    }

    private fun addThisKeyWordToEnvironment(variableEnvironment: VariableEnvironment, objectt: Object) {
        variableEnvironment.put(thisLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, objectt)
    }

    private fun crateSuperClassEnvironment(classs: Class, evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment) {
        classs.body.accept(evaluateVisitor, variableEnvironment)
    }

    //endregion
}
