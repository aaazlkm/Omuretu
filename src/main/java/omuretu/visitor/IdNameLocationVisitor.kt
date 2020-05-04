package omuretu.visitor

import lexer.token.IdToken
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.ast.statement.ConditionBlockStatement
import omuretu.ast.statement.DefStatement
import omuretu.ast.statement.ForStatement
import omuretu.ast.statement.IfStatement
import omuretu.ast.statement.ParametersStatement
import omuretu.ast.statement.RangeStatement
import omuretu.ast.statement.ValStatement
import omuretu.ast.statement.VarStatement
import omuretu.ast.statement.WhileStatement
import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.EnvironmentKey
import omuretu.exception.OmuretuException

class IdNameLocationVisitor : Visitor {
    //region expression

    fun visit(binaryExpression: BinaryExpression, idNameLocationMap: IdNameLocationMap) {
        val (left, operator, right) = binaryExpression
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", binaryExpression)
        left.accept(this, idNameLocationMap)
        right.accept(this, idNameLocationMap)
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

    fun visit(conditionBlockStatement: ConditionBlockStatement, idNameLocationMap: IdNameLocationMap) {
        val (condition, block) = conditionBlockStatement
        condition.accept(this, idNameLocationMap)
        val nestedIdNameLocationMap = IdNameLocationMap(idNameLocationMap)
        block.accept(this, nestedIdNameLocationMap)
        conditionBlockStatement.idNameSizeInBlock = nestedIdNameLocationMap.idNamesSize
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

    fun visit(forStatement: ForStatement, idNameLocationMap: IdNameLocationMap) {
        val (index, rangeStatement, blockStatement) = forStatement
        val nestedIdNameLocationMap = IdNameLocationMap(idNameLocationMap)

        nestedIdNameLocationMap.putAndReturnLocation(index.name).let {
            index.environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
        rangeStatement.accept(this, nestedIdNameLocationMap)
        blockStatement.accept(this, nestedIdNameLocationMap)
        forStatement.idNameSize = nestedIdNameLocationMap.idNamesSize
    }

    fun visit(ifStatement: IfStatement, idNameLocationMap: IdNameLocationMap) {
        val (conditionBlocks, elseBlock) = ifStatement
        conditionBlocks.forEach { it.accept(this, idNameLocationMap) }

        elseBlock?.let {
            val idNameLocationMapForElse = IdNameLocationMap(idNameLocationMap)
            elseBlock.accept(this, idNameLocationMapForElse)
            ifStatement.idNameSizeInElse = idNameLocationMapForElse.idNamesSize
        }
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

    fun visit(rangeStatement: RangeStatement, idNameLocationMap: IdNameLocationMap) {
        val (from, to) = rangeStatement
        from.accept(this, idNameLocationMap)
        to.accept(this, idNameLocationMap)
    }

    fun visit(valStatement: ValStatement, idNameLocationMap: IdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(valStatement.name).let {
            valStatement.environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
        valStatement.initializer.accept(this, idNameLocationMap)
    }

    fun visit(varStatement: VarStatement, idNameLocationMap: IdNameLocationMap) {
        idNameLocationMap.putAndReturnLocation(varStatement.name).let {
            varStatement.environmentKey = EnvironmentKey(it.ancestorAt, it.indexInIdNames)
        }
        varStatement.initializer.accept(this, idNameLocationMap)
    }

    fun visit(whileStatement: WhileStatement, idNameLocationMap: IdNameLocationMap) {
        val (condition, body) = whileStatement
        condition.accept(this, idNameLocationMap)

        val nestedIdNameLocationMap = IdNameLocationMap(idNameLocationMap)
        body.accept(this, nestedIdNameLocationMap)
        whileStatement.idNameSize = nestedIdNameLocationMap.idNamesSize
    }

    //endregion
}
