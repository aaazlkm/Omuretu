package omuretu.model

import omuretu.environment.Location
import omuretu.environment.NestedIdNameLocationMap
import omuretu.environment.base.VariableEnvironment
import omuretu.environment.NestedVariableEnvironment
import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.GlobalVariableEnvironment
import omuretu.exception.OmuretuException

data class Class(
        val classStmnt: ClassStatement,
        private val environment: GlobalVariableEnvironment,
        private val classMemberLocationMap: NestedIdNameLocationMap,
        private val thisLocation: Location
) {
    val superClass: Class?

    val body: ClassBodyStatement
        get() = classStmnt.bodyStmnt

    init {
        when (val superClassInfo = classStmnt.superClassName?.let { environment.getValueByIdName(it) }) {
            null -> this.superClass = null
            is Class -> this.superClass = superClassInfo
            else -> throw OmuretuException("unknown super class type $superClassInfo")
        }
    }

    override fun toString(): String {
        return "class: ${classStmnt.name}"
    }

    fun copyThisMembersTo(
            classMemberLocationMap: NestedIdNameLocationMap
    ) {
        classMemberLocationMap.copyFrom(this.classMemberLocationMap)
    }

    fun getMemberLocationOf(idName: String): Location? {
        return classMemberLocationMap.getLocationFromOnlyThisMap(idName)
    }

    fun createClassEnvironment(objectt: Object): NestedVariableEnvironment {
        val environment = NestedVariableEnvironment(classMemberLocationMap.idNamesSize, environment as? NestedVariableEnvironment)
        addThisKeyWordToEnvironment(environment, objectt)
        crateSuperClassEnvironment(this, environment)
        return environment
    }

    private fun addThisKeyWordToEnvironment(variableEnvironment: VariableEnvironment, objectt: Object) {
        variableEnvironment.put(thisLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, objectt)
    }

    private fun crateSuperClassEnvironment(classs: Class, variableEnvironment: VariableEnvironment) {
        if (classs.superClass != null) crateSuperClassEnvironment(classs.superClass, variableEnvironment)
        classs.body.evaluate(variableEnvironment)
    }
}