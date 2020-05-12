package omuretu.visitor

import lexer.token.IdToken
import omuretu.OMURETU_DEFAULT_RETURN_VALUE
import omuretu.OMURETU_FALSE
import omuretu.ast.expression.NegativeExpression
import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.LeftValueOperator
import omuretu.ast.expression.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.expression.binaryexpression.operator.base.RightValueOperator
import omuretu.ast.listeral.ArrayLiteral
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import omuretu.ast.postfix.ArgumentPostfix
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.ast.statement.BlockStatement
import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.ast.statement.DefStatement
import omuretu.ast.statement.ForStatement
import omuretu.ast.statement.IfStatement
import omuretu.ast.statement.NullStatement
import omuretu.ast.statement.RangeStatement
import omuretu.ast.statement.ValStatement
import omuretu.ast.statement.VarStatement
import omuretu.ast.statement.WhileStatement
import omuretu.environment.GlobalVariableEnvironment
import omuretu.environment.IdNameLocationMap
import omuretu.environment.VariableEnvironmentImpl
import omuretu.environment.base.VariableEnvironment
import omuretu.exception.OmuretuException
import omuretu.model.Class
import omuretu.model.Function
import omuretu.model.InlineCache
import omuretu.model.Object

class EvaluateVisitor : Visitor {
    //region expression

    fun visit(binaryExpression: BinaryExpression, variableEnvironment: VariableEnvironment): Any {
        val (left, operatorTree, right) = binaryExpression
        val inlineCache = binaryExpression.inlineCache
        val operatorToken = operatorTree.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", binaryExpression)
        val operator = OperatorDefinition.from(operatorToken.id)?.createOperator(left, right)
                ?: throw OmuretuException("cannnot evaluate:", binaryExpression)
        return when (operator) {
            is LeftValueOperator -> {
                operator.calculate(this, variableEnvironment, inlineCache) {
                    binaryExpression.inlineCache = it
                }
            }
            is RightValueOperator -> {
                operator.calculate(this, variableEnvironment)
            }
            else -> {
                throw OmuretuException("undefined operator: $operator ", binaryExpression)
            }
        }
    }

    fun visit(negativeExpression: NegativeExpression, variableEnvironment: VariableEnvironment): Any {
        val result = negativeExpression.operand.accept(this, variableEnvironment)
        return if (result is Int) {
            -result
        } else {
            throw OmuretuException("bad type for  -", negativeExpression)
        }
    }

    fun visit(primaryExpression: PrimaryExpression, variableEnvironment: VariableEnvironment): Any {
        val (literal, postFixes) = primaryExpression
        var result: Any = literal.accept(this, variableEnvironment)
        postFixes.forEach {
            result = it.accept(this, variableEnvironment, result)
        }
        return result
    }

    //endregion

    //region literal

    fun visit(arrayLiteral: ArrayLiteral, variableEnvironment: VariableEnvironment): Any {
        val results = mutableListOf<Any>()
        arrayLiteral.elements.forEach {
            results.add(it.accept(this, variableEnvironment))
        }
        return results
    }

    fun visit(idNameLiteral: IdNameLiteral, variableEnvironment: VariableEnvironment): Any {
        return idNameLiteral.environmentKey?.let { variableEnvironment.get(it) }
                ?: throw OmuretuException("undefined name: ${idNameLiteral.token.id}", idNameLiteral)
    }

    fun visit(numberLiteral: NumberLiteral, variableEnvironment: VariableEnvironment): Any {
        return numberLiteral.token.value
    }

    fun visit(stringLiteral: StringLiteral, variableEnvironment: VariableEnvironment): Any {
        return stringLiteral.token.string
    }

    //endregion

    //region postfix

    fun visit(argumentPostfix: ArgumentPostfix, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return when (leftValue) {
            is Function.OmuretuFunction -> {
                visitWhenOmuretuFunction(leftValue, argumentPostfix, variableEnvironment)
            }
            is Function.NativeFunction -> {
                visitWhenNativeFunction(leftValue, argumentPostfix, variableEnvironment)
            }
            else -> {
                throw OmuretuException("bad function type", argumentPostfix)
            }
        }
    }

    private fun visitWhenOmuretuFunction(function: Function.OmuretuFunction, argumentPostfix: ArgumentPostfix, variableEnvironment: VariableEnvironment): Any {
        val astTrees = argumentPostfix.astTrees
        if (astTrees.size != function.parameters.parameterNames.size) throw OmuretuException("bad number odf argument", argumentPostfix)
        val nestedEnvironment = VariableEnvironmentImpl(
                function.numberOfIdName,
                function.variableEnvironment as? VariableEnvironmentImpl
        )

        function.parameters.parameterEnvironmentKeys?.forEachIndexed { index, environmentKey ->
            nestedEnvironment.put(environmentKey, astTrees[index].accept(this, variableEnvironment))
        } ?: throw OmuretuException("cannnot find parameter location", argumentPostfix)

        return function.blockStatement.accept(this, nestedEnvironment)
    }

    private fun visitWhenNativeFunction(function: Function.NativeFunction, argumentPostfix: ArgumentPostfix, variableEnvironment: VariableEnvironment): Any {
        val astTrees = argumentPostfix.astTrees
        if (astTrees.size != function.numberOfParameter) throw OmuretuException("bad number of argument", argumentPostfix)
        val parameters = astTrees.map { it.accept(this, variableEnvironment) }.toTypedArray()
        return try {
            function.method.invoke(null, *parameters)
        } catch (exception: Exception) {
            throw OmuretuException("bad native function call: ${function.name}")
        }
    }

    fun visit(arrayPostfix: ArrayPostfix, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        val list = (leftValue as? MutableList<*>)?.mapNotNull { it } ?: throw OmuretuException("bad array access")
        val index = arrayPostfix.index.accept(this, variableEnvironment) as? Int ?: throw OmuretuException("bad array access")
        return list[index]
    }

    fun visit(dotPostfix: DotPostfix, variableEnvironment: VariableEnvironment, leftValue: Any): Any {
        return when (leftValue) {
            is Class -> visitWhenCalss(leftValue, dotPostfix)
            is Object -> visitWhenObject(leftValue, dotPostfix)
            else -> throw OmuretuException("bad member access: ", dotPostfix)
        }
    }

    private fun visitWhenCalss(classs: Class, dotPostfix: DotPostfix): Any {
        if (dotPostfix.name == DotPostfix.KEYWORD_NEW) {
            // インスタンス化
            val objectt = Object(classs)
            val objectEnvironment = classs.createClassEnvironment(objectt, this)
            objectt.variableEnvironment = objectEnvironment
            return objectt
        } else {
            throw OmuretuException("bad member access: ", dotPostfix)
        }
    }

    private fun visitWhenObject(objectt: Object, dotPostfix: DotPostfix): Any {
        val inlineCache = dotPostfix.objectInlineCache
        return if (objectt.classs == inlineCache?.classs) {
            objectt.getMember(inlineCache.location) ?: throw OmuretuException("bad member access: ", dotPostfix)
        } else {
            val memberLocaiton = objectt.getMemberLocationOf(dotPostfix.name) ?: throw OmuretuException("bad member access: ", dotPostfix)
            dotPostfix.objectInlineCache = InlineCache(objectt.classs, memberLocaiton)
            objectt.getMember(memberLocaiton) ?: throw OmuretuException("bad member access: ", dotPostfix)
        }
    }

    //endregion

    fun visit(blockStatement: BlockStatement, variableEnvironment: VariableEnvironment): Any {
        var result: Any? = null
        blockStatement.astTrees.forEach {
            result = it.accept(this, variableEnvironment)
        }
        return result ?: OMURETU_DEFAULT_RETURN_VALUE // FIXME うまくない
    }

    fun visit(classBodyStatement: ClassBodyStatement, variableEnvironment: VariableEnvironment): Any {
        classBodyStatement.members.forEach { it.accept(this, variableEnvironment) }
        // `environment`にmemberを追加するだけでいいので、返り値はなんでもいい
        return OMURETU_DEFAULT_RETURN_VALUE
    }

    fun visit(classStatement: ClassStatement, variableEnvironment: VariableEnvironment): Any {
        val name = classStatement.name
        val bodyStatement = classStatement.bodyStatement
        val globalEnvironment = variableEnvironment as? GlobalVariableEnvironment
                ?: throw OmuretuException("class can define only in global environment")
        val classMemberLocationMap = IdNameLocationMap(globalEnvironment.idNameLocationMap)
        // クラスボディのメンバー最初にthisを追加
        val thisLocation = classMemberLocationMap.putOnlyThisMapAndReturnLocation("this")
        val classs = Class(classStatement, globalEnvironment, classMemberLocationMap, thisLocation)

        // 継承先のクラスのメンバーをコピー
        classs.superClass?.copyThisMembersTo(classMemberLocationMap)

        // クラスボディ内の変数の位置
        bodyStatement.accept(IdNameLocationVisitor(), classMemberLocationMap)

        // クラス名を登録
        globalEnvironment.putValueByIdName(name, classs)

        return name
    }

    fun visit(defStatement: DefStatement, variableEnvironment: VariableEnvironment): Any {
        val (name, parameters, typeStatement, blockStatement) = defStatement
        val environmentKey = defStatement.environmentKey ?: throw OmuretuException("donot defined def name $name")
        val idNamesSize = defStatement.idNamesInDefSize ?: throw OmuretuException("cannnot get idNamesSize $name")
        variableEnvironment.put(environmentKey, Function.OmuretuFunction(parameters, blockStatement, variableEnvironment, idNamesSize))
        return name
    }

    fun visit(forStatement: ForStatement, variableEnvironment: VariableEnvironment): Any {
        val (index, rangeStatement, blockStatement) = forStatement
        val nestedVariableEnvironment = VariableEnvironmentImpl(forStatement.idNameSize, variableEnvironment as? VariableEnvironmentImpl)
        val (from, to, step) = rangeStatement.accept(this, nestedVariableEnvironment) as RangeStatement.EvaluatedValue
        val indexEnvironmentKey = index.environmentKey ?: throw OmuretuException("undefined", forStatement)

        nestedVariableEnvironment.put(indexEnvironmentKey, from)
        if (from <= to) {
            for (i in from..to step step) {
                nestedVariableEnvironment.put(indexEnvironmentKey, i)
                blockStatement.accept(this, nestedVariableEnvironment)
            }
        } else {
            for (i in from downTo to step step) {
                nestedVariableEnvironment.put(indexEnvironmentKey, i)
                blockStatement.accept(this, nestedVariableEnvironment)
            }
        }
        return OMURETU_DEFAULT_RETURN_VALUE
    }

    fun visit(ifStatement: IfStatement, variableEnvironment: VariableEnvironment): Any {
        val (conditionBlocks, elseBlock) = ifStatement
        conditionBlocks.forEach {
            val (condition, block) = it
            val conditionResult = condition.accept(this, variableEnvironment)
            if (conditionResult != OMURETU_FALSE) {
                val nestedVariableEnvironment = VariableEnvironmentImpl(it.idNameSizeInBlock, variableEnvironment as? VariableEnvironmentImpl)
                return block.accept(this, nestedVariableEnvironment)
            }
        }
        return elseBlock?.let {
            val nestedVariableEnvironment = VariableEnvironmentImpl(ifStatement.idNameSizeInElse, variableEnvironment as? VariableEnvironmentImpl)
            it.accept(this, nestedVariableEnvironment)
        } ?: OMURETU_DEFAULT_RETURN_VALUE
    }

    fun visit(nullStatement: NullStatement, variableEnvironment: VariableEnvironment): Any {
        return OMURETU_DEFAULT_RETURN_VALUE
    }

    fun visit(rangeStatement: RangeStatement, variableEnvironment: VariableEnvironment): Any {
        val (from, to, step) = rangeStatement
        val fromValue = from.accept(this, variableEnvironment) as? Int ?: throw OmuretuException("range from should be number", rangeStatement)
        val toValue = to.accept(this, variableEnvironment) as? Int ?: throw OmuretuException("range to should be number", rangeStatement)
        val stepValue = step?.accept(this, variableEnvironment) as? Int ?: 1
        if (stepValue <= 0) throw OmuretuException("step value should be positibe value", rangeStatement)
        return RangeStatement.EvaluatedValue(
                fromValue,
                toValue,
                stepValue
        )
    }

    fun visit(valStatement: ValStatement, variableEnvironment: VariableEnvironment): Any {
        val environmentKey = valStatement.environmentKey ?: throw OmuretuException("undefined", valStatement)
        val value = valStatement.initializer.accept(this, variableEnvironment)
        variableEnvironment.put(environmentKey, value)
        return value
    }

    fun visit(varStatement: VarStatement, variableEnvironment: VariableEnvironment): Any {
        val environmentKey = varStatement.environmentKey ?: throw OmuretuException("undefined", varStatement)
        val value = varStatement.initializer.accept(this, variableEnvironment)
        variableEnvironment.put(environmentKey, value)
        return value
    }

    fun visit(whileStatement: WhileStatement, variableEnvironment: VariableEnvironment): Any {
        val (condition, body) = whileStatement
        var bodyResult: Any = OMURETU_DEFAULT_RETURN_VALUE
        while (true) {
            val conditionResult = condition.accept(this, variableEnvironment)
            if (conditionResult is Int && conditionResult != OMURETU_FALSE) {
                val nestedVariableEnvironment = VariableEnvironmentImpl(whileStatement.idNameSize, variableEnvironment as? VariableEnvironmentImpl)
                bodyResult = body.accept(this, nestedVariableEnvironment)
            } else {
                return bodyResult
            }
        }
    }
}
