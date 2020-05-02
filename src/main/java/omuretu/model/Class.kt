package omuretu.model

import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.IdNameLocationMap
import omuretu.environment.Location
import omuretu.environment.VariableEnvironmentImpl
import omuretu.environment.base.EnvironmentKey
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.visitor.EvaluateVisitor

data class Class(
        val classStmnt: ClassStatement,
        private val environment: GlobalVariableEnvironment,
        private val classMemberLocationMap: IdNameLocationMap,
        private val thisLocation: Location
) {
    val superClass: Class?

    val body: ClassBodyStatement
        get() = classStmnt.bodyStatement

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
            classMemberLocationMap: IdNameLocationMap
    ) {
        classMemberLocationMap.copyFrom(this.classMemberLocationMap)
    }

    fun getMemberLocationOf(idName: String): Location? {
        return classMemberLocationMap.getLocationFromOnlyThisMap(idName)
    }

    fun createClassEnvironment(objectt: Object, evaluateVisitor: EvaluateVisitor): VariableEnvironmentImpl {
        val environment = VariableEnvironmentImpl(classMemberLocationMap.idNamesSize, environment as? VariableEnvironmentImpl)
        addThisKeyWordToEnvironment(environment, objectt)
        crateSuperClassEnvironment(this, evaluateVisitor, environment)
        return environment
    }

    private fun addThisKeyWordToEnvironment(variableEnvironment: VariableEnvironment, objectt: Object) {
        variableEnvironment.put(thisLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, objectt)
    }

    private fun crateSuperClassEnvironment(classs: Class, evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment) {
        if (classs.superClass != null) crateSuperClassEnvironment(classs.superClass, evaluateVisitor, variableEnvironment)
        classs.body.accept(evaluateVisitor, variableEnvironment)
    }
}