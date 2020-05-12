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
import omuretu.ast.statement.BlockStatement
import omuretu.ast.statement.ClassBodyStatement
import omuretu.ast.statement.ClassStatement
import omuretu.ast.statement.ConditionBlockStatement
import omuretu.ast.statement.DefStatement
import omuretu.ast.statement.ForStatement
import omuretu.ast.statement.IfStatement
import omuretu.ast.statement.ParameterStatement
import omuretu.ast.statement.ParametersStatement
import omuretu.ast.statement.ValStatement
import omuretu.ast.statement.VarStatement
import omuretu.ast.statement.WhileStatement
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
        val operatorToken = binaryExpression.operator.token as? IdToken ?: throw OmuretuException("cannnot check type:", binaryExpression)
        val operator = OperatorDefinition.from(operatorToken.id)?.createOperator(left, right)
                ?: throw OmuretuException("cannnot cehck type:", binaryExpression)
        return operator.checkType(this, typeEnvironment)
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
        val arrayType = arrayLiteral.elements
                .map { it.accept(this, typeEnvironment) }
                .fold(Type.NeedInference() as Type) { acc, now -> TypeCheckHelper.union(acc, now, typeEnvironment) }

        return when (arrayType) {
            is Type.NeedInference -> {
                // TODO 要素が存在しない場合は、型を指定するようにする
                Type.Defined.Array(Type.Defined.Any())
            }
            is Type.Defined -> {
                Type.Defined.Array(arrayType)
            }
        }
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
        val arrayType = leftType as? Type.Defined.Array ?: throw OmuretuException("leftType $leftType should be array type", arrayPostfix)
        return arrayType.type
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

    fun visit(conditionBlockStatement: ConditionBlockStatement, typeEnvironment: TypeEnvironment): Type {
        val (condition, block) = conditionBlockStatement
        val conditionType = condition.accept(this, typeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int(), conditionBlockStatement, typeEnvironment)

        val nestedTypeEnvironment = TypeEnvironmentImpl(typeEnvironment)
        return block.accept(this, nestedTypeEnvironment)
    }

    fun visit(defStatement: DefStatement, typeEnvironment: TypeEnvironment): Type {
        val (idNameLiteral, parameters, typeTag, blockStatement) = defStatement
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

    fun visit(forStatement: ForStatement, typeEnvironment: TypeEnvironment): Type {
        val indexEnvironmentKey = forStatement.index.environmentKey ?: throw OmuretuException("undefined ${forStatement.index.name}")
        val nestedTypeEnvironment = TypeEnvironmentImpl(typeEnvironment)
        nestedTypeEnvironment.put(indexEnvironmentKey, Type.Defined.Int())
        return forStatement.blockStatement.accept(this, nestedTypeEnvironment)
    }

    fun visit(ifStatement: IfStatement, typeEnvironment: TypeEnvironment): Type {
        val (conditionBlocks, elseBlock) = ifStatement
        val blockTypes = conditionBlocks.map { it.accept(this, typeEnvironment) }

        val elseBlockType = elseBlock?.let {
            val typeEnvironmentForElse = TypeEnvironmentImpl(typeEnvironment)
            it.accept(this, typeEnvironmentForElse)
        }

        return if (elseBlockType == null) {
            blockTypes.reduce { acc, now -> TypeCheckHelper.union(acc, now, typeEnvironment) }
        } else {
            val blockType = blockTypes.reduce { acc, now -> TypeCheckHelper.union(acc, now, typeEnvironment) }
            TypeCheckHelper.union(blockType, elseBlockType, typeEnvironment)
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
        val nestedTypeEnvironment = TypeEnvironmentImpl(typeEnvironment)
        val bodyType = body.accept(this, nestedTypeEnvironment)
        TypeCheckHelper.checkSubTypeOrThrow(conditionType, Type.Defined.Int(), whileStatement, nestedTypeEnvironment)
        return TypeCheckHelper.union(bodyType, Type.Defined.Int(), nestedTypeEnvironment) // whileのbodyが一度も実行されない場合Intを返すためunion(Type.Int)している
    }

    //endregion
}
