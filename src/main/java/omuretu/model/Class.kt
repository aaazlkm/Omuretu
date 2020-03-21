package omuretu.model

import omuretu.environment.Environment
import omuretu.environment.NestedEnvironment
import omuretu.ast.statement.ClassBodyStmnt
import omuretu.ast.statement.ClassStmnt

data class Class(
        val classStmnt: ClassStmnt,
        private val environment: Environment
) {
    val superClass: Class?

    val body: ClassBodyStmnt
        get() = classStmnt.bodyStmnt

    init {
        this.superClass = null
//        when (val superClassInfo = classStmnt.superClassName?.let { environment.get(it) }) {
//            null -> this.superClass = null
//            is Class -> this.superClass = superClassInfo
//            else -> throw OmuretuException("unknown super class type $superClassInfo")
//        }
    }

    override fun toString(): String {
        return "class: ${classStmnt.name}"
    }

    fun createClassEnvironment(): NestedEnvironment {
        val environment = NestedEnvironment(10, environment as? NestedEnvironment)
        crateEachClassEnvironment(this, environment)
        return environment
    }

    private fun crateEachClassEnvironment(classs: Class, environment: Environment) {
        if (classs.superClass != null) crateEachClassEnvironment(classs.superClass, environment)
        classs.body.evaluate(environment)
    }
}