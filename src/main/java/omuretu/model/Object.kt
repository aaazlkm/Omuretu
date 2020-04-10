package omuretu.model

import omuretu.Location
import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.exception.AccessException

data class Object(
        val classs: Class
) {
    lateinit var environment: Environment

    fun getMember(location: Location): Any? {
        return environment.get(location.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) })
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
                environment.put(memberLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, member)
            }
        }
    }
}