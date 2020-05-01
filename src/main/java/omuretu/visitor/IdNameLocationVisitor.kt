package omuretu.visitor

import lexer.token.IdToken
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.statement.*
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
import omuretu.exception.OmuretuException

class IdNameLocationVisitor: Visitor {
    //region expression

    fun visit(binaryExpression: BinaryExpression, idNameLocationMap: IdNameLocationMap) {
        val (left, operator, right) = binaryExpression
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", binaryExpression)
        when {
            operatorToken.id == OperatorDefinition.ASSIGNMENT.rawOperator && left is IdNameLiteral -> { // FIXME うまくない気がする
                left.lookupIdNamesForAssign(idNameLocationMap)
                right.accept(this, idNameLocationMap)
            }
            else -> {
                left.accept(this, idNameLocationMap)
                right.accept(this, idNameLocationMap)
            }
        }
    }

    //endregion

    //region literal

    fun visit(idNameLiteral: IdNameLiteral, idNameLocationMap: IdNameLocationMap) {
        idNameLocationMap.getLocationFromAllMap(idNameLiteral.name)?.let {
            idNameLiteral.environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        } ?: throw OmuretuException("undefined name: $idNameLiteral")
    }

    //endregion

    //region statement

    fun visit(classBodyStatement: ClassBodyStatement, idNameLocationMap: IdNameLocationMap) {
        classBodyStatement.members.forEach { it.accept(this, idNameLocationMap) }
    }

    fun visit(classStatement: ClassStatement, idNameLocationMap: IdNameLocationMap) {
        // スーパークラスを持つ場合環境がないとスーパークラスの定義を取得することができないためここでは何もしない
    }

    fun visit(defStatement: DefStatement, idNameLocationMap: IdNameLocationMap) {
        val name = defStatement.name
        val parameters = defStatement.parameters
        val blockStatement = defStatement.blockStatement
        val location = idNameLocationMap.putAndReturnLocation(name)
        defStatement.environmentKey = EnvironmentKey(location.ancestorAt, location.indexInIdNames)

        val nestIdNameLocationMap = IdNameLocationMap(idNameLocationMap)
        parameters.accept(this, nestIdNameLocationMap)
        blockStatement.accept(this, nestIdNameLocationMap)
        defStatement.idNamesInDefSize = nestIdNameLocationMap.idNamesSize
    }

    fun visit(parametersStatement: ParametersStatement, idNameLocationMap: IdNameLocationMap) {
        val parameters = parametersStatement.parameters
        val parameterLocation = arrayOfNulls<EnvironmentKey>(parameters.size)
        parameters.forEachIndexed { index, idNameLiteral ->
            val location = idNameLocationMap.putAndReturnLocation(idNameLiteral.name)
            parameterLocation[index] = EnvironmentKey(location.ancestorAt, location.indexInIdNames)
        }
        parametersStatement.parameterEnvironmentKeys = parameterLocation.mapNotNull { it }.toTypedArray()
    }

    fun visit(varStatement: VarStatement, idNameLocationMap: IdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(varStatement.name).let {
            varStatement.environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
        varStatement.initializer.accept(this, idNameLocationMap)
    }
    //endregion

}