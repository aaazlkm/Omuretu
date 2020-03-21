package omuretu.model

import omuretu.environment.NestedEnvironment
import omuretu.exception.AccessException

data class Object(
        private val environment: NestedEnvironment
) {
    // 外のenviromentから変数を取得使用した場合Accessエラーを投げる
    fun getMember(memberName: String): Any? {
//        val environment = environment.searchEnvironmentHasThisKey(memberName) ?: run {
//            throw AccessException("there is not defined this member $memberName")
//        }
//        if (this.environment == environment) {
//            return this.environment.get(memberName)
//        } else {
//            throw AccessException("you cannot access this member $memberName")
//        }
        throw AccessException("you cannot access this member $memberName")

    }

    fun putMember(memberName: String, member: Any) {
//        environment.putOnlyThisEnvironment(memberName, member)
    }
}