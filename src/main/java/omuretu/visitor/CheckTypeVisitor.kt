package omuretu.visitor

import lexer.token.IdToken
import omuretu.ast.expression.NegativeExpression
import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.ArrayLiteral
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import omuretu.ast.postfix.ArgumentPostfix
import omuretu.ast.postfix.ArrayPostfix
import omuretu.ast.postfix.DotPostfix
import omuretu.ast.statement.*
import omuretu.environment.TypeEnvironmentImpl
import omuretu.environment.base.TypeEnvironment
import omuretu.exception.OmuretuException
import omuretu.typechecker.Type
import omuretu.typechecker.TypeCheckHelper


class CheckTypeVisitor : Visitor {
    //region expression

    fun visit(binaryExpression: BinaryExpression, typeEnvironment: TypeEnvironment): Type {
        val left = binaryExpression.left
        val right = binaryExpression.right
        val operator = binaryExpression.operator
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot evaluate:", binaryExpression)

        if (operatorToken.id == OperatorDefinition.ASSIGNMENT.rawOperator) {
            val rightType = right.accept(this, typeEnvironment)
            val leftIdName = left as? IdNameLiteral ?: throw OmuretuException("cannnot compile:", binaryExpression)
            return leftIdName.checkTypeForAssign(typeEnvironment, rightType)
        }

        val leftType = left.accept(this, typeEnvironment)
        val rightType = right.accept(this, typeEnvironment)
        return when (operatorToken.id) {
            OperatorDefinition.EQUAL.rawOperator -> {
                Type.Defined.Int()
            }
            OperatorDefinition.PLUS.rawOperator -> {
                TypeCheckHelper.plus(leftType, rightType, typeEnvironment)
            }
            else -> {
                TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int(), leftType, binaryExpression, typeEnvironment)
                TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int(), rightType, binaryExpression, typeEnvironment)
                Type.Defined.Int()
            }
        }
    }

    fun visit(negativeExpression: NegativeExpression, typeEnvironment: TypeEnvironment): Type {
        val operand = negativeExpression.operand
        val type = operand.accept(this, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(Type.Defined.Int(), type, negativeExpression, typeEnvironment)
        return type
    }

    fun visit(primaryExpression: PrimaryExpression, typeEnvironment: TypeEnvironment): Type {
        val literal = primaryExpression.literal
        val postFixes = primaryExpression.postFixes
        var result = literal.accept(this, typeEnvironment)
        postFixes.forEach {
            result = it.accept(this, typeEnvironment, result)
        }
        return result
    }

    //endregion

    //region literal

    fun visit(arrayLiteral: ArrayLiteral, typeEnvironment: TypeEnvironment): Type {
        // TODO 配列型を返すようにする
        return Type.Defined.Any()
    }

    fun visit(idNameLiteral: IdNameLiteral, typeEnvironment: TypeEnvironment): Type {
        return idNameLiteral.environmentKey?.let { typeEnvironment.get(it) } ?: throw OmuretuException("undefined name: $idNameLiteral")
    }

    fun visit(numberLiteral: NumberLiteral, typeEnvironment: TypeEnvironment): Type {
        return Type.Defined.Int()
    }

    fun visit(stringLiteral: StringLiteral, typeEnvironment: TypeEnvironment): Type {
        return Type.Defined.String()
    }

    //endregion

    //region postfix

    fun visit(argumentPostfix: ArgumentPostfix, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        val astTrees = argumentPostfix.astTrees
        val functionType = leftType as? Type.Defined.Function ?: throw OmuretuException("bad left type", argumentPostfix)
        if (astTrees.size != functionType.parameterTypes.size) throw OmuretuException("bad number of argument", argumentPostfix)
        functionType.parameterTypes
                .zip(astTrees.map { it.accept(this, typeEnvironment) })
                .forEach { TypeCheckHelper.checkSubTypeOrThrow(it.first, it.second, argumentPostfix, typeEnvironment) }
        return functionType.returnType
    }

    fun visit(arrayPostfix: ArrayPostfix, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        return Type.Defined.Any()
    }

    fun visit(dotPostfix: DotPostfix, typeEnvironment: TypeEnvironment, leftType: Type): Type {
        // TODO クラスのプロパティの型を見るようにする
        return Type.Defined.Class()
    }

    //endregion

    //region statement

    fun visit(blockStatement: BlockStatement, typeEnvironment: TypeEnvironment): Type {
        val astTrees = blockStatement.astTrees
        return astTrees.map { it.accept(this, typeEnvironment) }.lastOrNull() ?: Type.Defined.Int()
    }

    fun visit(classBodyStatement: ClassBodyStatement, typeEnvironment: TypeEnvironment): Type {
        // TODO クラス型を用意する
        return Type.Defined.Any()
    }

    fun visit(classStatement: ClassStatement, typeEnvironment: TypeEnvironment): Type {
        // TODO クラス型を用意する
        return Type.Defined.Any()
    }

    fun visit(defStatement: DefStatement, typeEnvironment: TypeEnvironment): Type {
        val (idNameLiteral,parameters, typeTag, blockStatement) = defStatement
        val environmentKey = defStatement.environmentKey ?: throw OmuretuException("donot defined $this")
        val returnType = typeTag.type as? Type.Defined ?: Type.Defined.Unit()
        val parameterTypes = parameters.types
        if (parameters.parameterNames.size != parameterTypes.size) throw OmuretuException("failed to convert parameter type :from ${parameters.parameterNames} to $parameterTypes")
        val functionType = Type.Defined.Function(returnType, parameterTypes)
        typeEnvironment.put(environmentKey, functionType)
        val bodyTypeEnvironment = TypeEnvironmentImpl(typeEnvironment)
        parameters.accept(this, bodyTypeEnvironment)
        blockStatement.accept(this, bodyTypeEnvironment)
        return functionType
    }

    fun visit(ifStatement: IfStatement, typeEnvironment: TypeEnvironment): Type {
        val (condition, thenBlock, elseBlock) = ifStatement
        val conditionType = condition.accept(this, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int(), ifStatement, typeEnvironment)
        val thenBlockType = thenBlock.accept(this, typeEnvironment)
        val elseBlockType = elseBlock?.accept(this, typeEnvironment)
        return if (elseBlockType == null) {
            thenBlockType
        } else {
            TypeCheckHelper.union(thenBlockType, elseBlockType, typeEnvironment)
        }
    }

    fun visit(parametersStatement: ParametersStatement, typeEnvironment: TypeEnvironment): Type {
        val parameters = parametersStatement.parameters
        val parameterEnvironmentKeys = parametersStatement.parameterEnvironmentKeys ?: throw OmuretuException("")
        if (parameters.size != parameterEnvironmentKeys.size) throw OmuretuException("")
        parameterEnvironmentKeys.zip(parameters).forEach { typeEnvironment.put(it.first, it.second.accept(this, typeEnvironment)) }
        return Type.Defined.Any() // パラメータ全体の型はなんでもいい
    }

    fun visit(parameterStatement: ParameterStatement, typeEnvironment: TypeEnvironment): Type {
        return parameterStatement.type
    }

    fun visit(valStatement: ValStatement, typeEnvironment: TypeEnvironment): Type {
        val (idNameLiteral, typeTag, initializer) = valStatement
        val environmentKey = valStatement.environmentKey ?: throw OmuretuException("undefined", valStatement)
        if (typeEnvironment.get(environmentKey) != null) throw OmuretuException("duplicate variable ${idNameLiteral.name}", valStatement)
        val initializerType = initializer.accept(this, typeEnvironment)
        typeTag.type?.let { TypeCheckHelper.checkSubTypeOrThrow(it, initializerType, valStatement, typeEnvironment) }
        typeEnvironment.put(environmentKey, initializerType)
        return initializerType
    }

    fun visit(varStatement: VarStatement, typeEnvironment: TypeEnvironment): Type {
        val (idNameLiteral, typeTag, initializer) = varStatement
        val environmentKey = varStatement.environmentKey ?: throw OmuretuException("undefined", varStatement)
        if (typeEnvironment.get(environmentKey) != null) throw OmuretuException("duplicate variable ${idNameLiteral.name}", varStatement)
        val initializerType = initializer.accept(this, typeEnvironment)
        initializerType.readOnly = false
        typeTag.type?.let { TypeCheckHelper.checkSubTypeOrThrow(it, initializerType, varStatement, typeEnvironment) }
        typeEnvironment.put(environmentKey, initializerType)
        return initializerType
    }

    fun visit(whileStatement: WhileStatement, typeEnvironment: TypeEnvironment): Type {
        val (condition, body) = whileStatement
        val conditionType = condition.accept(this, typeEnvironment)
        val bodyType = body.accept(this, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int(), whileStatement, typeEnvironment)
        return TypeCheckHelper.union(bodyType, Type.Defined.Int(), typeEnvironment) // whileのbodyが一度も実行されない場合Intを返すためunion(Type.Int)している
    }

    //endregion
}
