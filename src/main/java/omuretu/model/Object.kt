package omuretu.model

import omuretu.environment.Environment
import omuretu.environment.EnvironmentKey
import omuretu.exception.AccessException

data class Object(
        private val classs: Class
) {
    lateinit var environment: Environment

    // 外のenviromentから変数を取得使用した場合Accessエラーを投げる
    fun getMember(memberName: String): Any? {
        val memberLocation = classs.getMemberLocationOf(memberName) ?: throw AccessException("there is no member $memberName")
        return environment.get(memberLocation.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) })
    }

    fun putMember(memberName: String, member: Any) {
        // メソッドの時は更新できないようにする
        when(getMember(memberName)) {
            is Function -> {
                throw AccessException("cannnot assigun function $memberName")
            }
            else -> {
                val memberIndex = classs.getMemberLocationOf(memberName) ?: throw AccessException("there is no member $memberName")
                environment.put(memberIndex.let { EnvironmentKey(it.ancestorAt, it.indexInIdNames) }, member)
            }
        }
    }
}