package omuretu.model

import omuretu.environment.Location
import omuretu.environment.base.VariableEnvironment
import omuretu.environment.base.EnvironmentKey
import omuretu.exception.AccessException

data class Object(
        val classs: Class
) {
    lateinit var variableEnvironment: VariableEnvironment

    fun getMember(location: Location): Any? {
        return variableEnvironment.get(location.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) })
    }

    fun getMemberLocationOf(name: String): Location? {
        return classs.getMemberLocationOf(name)
    }

    fun putMember(memberLocation: Location, member: Any) {
        when (getMember(memberLocation)) {
            is Function -> {
                // メソッドの時は更新できないようにする
                throw AccessException("cannnot assign function $memberLocation")
            }
            else -> {
                variableEnvironment.put(memberLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, member)
            }
        }
    }
}