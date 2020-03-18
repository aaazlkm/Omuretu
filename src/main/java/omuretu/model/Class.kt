package omuretu.model

import omuretu.Environment
import omuretu.NestedEnvironment
import omuretu.ast.ClassBodyStmnt
import omuretu.ast.ClassStmnt
import omuretu.exception.OmuretuException

data class Class(
        val classStmnt: ClassStmnt,
        private val environment: Environment
) {
    val superClass: Class?

    val body: ClassBodyStmnt
        get() = classStmnt.bodyStmnt

    init {
        when (val superClassInfo = classStmnt.superClassName?.let { environment.get(it) }) {
            null -> this.superClass = null
            is Class -> this.superClass = superClassInfo
            else -> throw OmuretuException("unknown super class type $superClassInfo")
        }
    }

    override fun toString(): String {
        return "class: ${classStmnt.name}"
    }

    fun createClassEnvironment(): NestedEnvironment {
        val environment = NestedEnvironment(environment as? NestedEnvironment)
        crateEachClassEnvironment(this, environment)
        return environment
    }

    private fun crateEachClassEnvironment(classs: Class, environment: Environment) {
        if (classs.superClass != null) crateEachClassEnvironment(classs.superClass, environment)
        classs.body.evaluate(environment)
    }
}