package omuretu.model

import omuretu.Location
import omuretu.NestedIdNameLocationMap
import omuretu.environment.Environment
import omuretu.environment.NestedEnvironment
import omuretu.ast.statement.ClassBodyStmnt
import omuretu.ast.statement.ClassStmnt
import omuretu.ast.statement.DefStmnt
import omuretu.environment.EnvironmentKey
import omuretu.environment.GlobalEnvironment
import omuretu.exception.OmuretuException

data class Class(
        val classStmnt: ClassStmnt,
        private val environment: GlobalEnvironment,
        private val classMemberLocationMap: NestedIdNameLocationMap,
        private val thisLocation: Location
) {
    val superClass: Class?

    val body: ClassBodyStmnt
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

    fun createClassEnvironment(objectt: Object): NestedEnvironment {
        val environment = NestedEnvironment(classMemberLocationMap.idNamesSize, environment as? NestedEnvironment)
        addThisKeyWordToEnvironment(environment, objectt)
        crateSuperClassEnvironment(this, environment)
        return environment
    }

    private fun addThisKeyWordToEnvironment(environment: Environment, objectt: Object) {
        environment.put(thisLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, objectt)
    }

    private fun crateSuperClassEnvironment(classs: Class, environment: Environment) {
        if (classs.superClass != null) crateSuperClassEnvironment(classs.superClass, environment)
        classs.body.evaluate(environment)
    }
}