package omuretu.visitor

import lexer.token.IdToken
import omuretu.ast.expression.NegativeExpression
import omuretu.ast.expression.PrimaryExpression
import omuretu.ast.expression.binaryexpression.BinaryExpression
import omuretu.ast.expression.binaryexpression.operator.base.OperatorDefinition
import omuretu.ast.listeral.IdNameLiteral
import omuretu.ast.listeral.NumberLiteral
import omuretu.ast.listeral.StringLiteral
import omuretu.ast.postfix.ArgumentPostfix
import omuretu.ast.statement.BlockStatement
import omuretu.ast.statement.DefStatement
import omuretu.ast.statement.IfStatement
import omuretu.ast.statement.WhileStatement
import omuretu.exception.OmuretuException
import omuretu.virtualmachine.ByteCodeStore
import omuretu.virtualmachine.OmuretuVirtualMachine
import omuretu.virtualmachine.opecode.BConstOpecode
import omuretu.virtualmachine.opecode.CallOpecode
import omuretu.virtualmachine.opecode.GmoveOpecode
import omuretu.virtualmachine.opecode.GotoOpecode
import omuretu.virtualmachine.opecode.IConstOpecode
import omuretu.virtualmachine.opecode.IfZeroOpecode
import omuretu.virtualmachine.opecode.MoveOpecode
import omuretu.virtualmachine.opecode.NegOpecode
import omuretu.virtualmachine.opecode.RestoreOpecode
import omuretu.virtualmachine.opecode.ReturnOpecode
import omuretu.virtualmachine.opecode.SConstOpecode
import omuretu.virtualmachine.opecode.SaveOpecode
import omuretu.virtualmachine.opecode.base.ComputeOpecode
import util.ex.sliceByByte

class CompileVisitor : Visitor {
    //region expression

    fun visit(binaryExpression: BinaryExpression, byteCodeStore: ByteCodeStore) {
        val (left, operator, right) = binaryExpression
        val operatorToken = operator.token as? IdToken ?: throw OmuretuException("cannnot compile:", binaryExpression)
        if (operatorToken.id == OperatorDefinition.ASSIGNMENT.rawOperator) {
            val leftIdName = left as? IdNameLiteral ?: throw OmuretuException("cannnot compile:", binaryExpression)
            right.accept(this, byteCodeStore)
            leftIdName.compileAssign(byteCodeStore)
        } else {
            left.accept(this, byteCodeStore)
            right.accept(this, byteCodeStore)
            val registerLeftAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 2)
            val registerRightAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
            ComputeOpecode.createByteCode(operatorToken.id, registerLeftAt, registerRightAt).forEach {
                byteCodeStore.addByteCode(it)
            }
            byteCodeStore.prevRegister()
        }
    }

    fun visit(negativeExpression: NegativeExpression, byteCodeStore: ByteCodeStore) {
        negativeExpression.operand.accept(this, byteCodeStore)
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
        NegOpecode.createByteCode(registerAt).forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    fun visit(primaryExpression: PrimaryExpression, byteCodeStore: ByteCodeStore) {
        val (literal, postFixes) = primaryExpression
        literal.accept(this, byteCodeStore)
        postFixes.forEach {
            it.accept(this, byteCodeStore)
        }
    }

    //endregion

    //region literal

    fun visit(idNameLiteral: IdNameLiteral, byteCodeStore: ByteCodeStore) {
        val environmentKey = idNameLiteral.environmentKey ?: throw OmuretuException("undefined name: $idNameLiteral")
        when {
            environmentKey.ancestorAt > 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
                GmoveOpecode.createByteCode(environmentKey.index.toShort(), registerAt).forEach { byteCodeStore.addByteCode(it) }
            }
            environmentKey.ancestorAt == 0 -> {
                val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
                MoveOpecode.createByteCode(environmentKey.index.toByte(), registerAt).forEach { byteCodeStore.addByteCode(it) }
            }
            else -> {
                throw OmuretuException("undefined name: $idNameLiteral")
            }
        }
    }

    fun visit(numberLiteral: NumberLiteral, byteCodeStore: ByteCodeStore) {
        val value = numberLiteral.value
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        val byteCodes = if (value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            BConstOpecode.createByteCode(value.toByte(), registerAt)
        } else {
            IConstOpecode.createByteCode(value, registerAt)
        }
        byteCodes.forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    fun visit(stringLiteral: StringLiteral, byteCodeStore: ByteCodeStore) {
        val index = byteCodeStore.strings.size - 1
        byteCodeStore.strings[index] = stringLiteral.string
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        SConstOpecode.createByteCode(index.toShort(), registerAt).forEach {
            byteCodeStore.addByteCode(it)
        }
    }

    //endregion

    //region postfix

    fun visit(argumentPostfix: ArgumentPostfix, byteCodeStore: ByteCodeStore) {
        val astTrees = argumentPostfix.astTrees
        // defStmntのcompileメソッドでstackFrameSizeに格納している
        var offset = byteCodeStore.stackFrameSize.toByte()

        astTrees.forEach {
            it.accept(this, byteCodeStore)
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
            MoveOpecode.createByteCode(registerAt, offset++).forEach { byteCodeStore.addByteCode(it) }
        }

        var registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        CallOpecode.createByteCode(registerAt, astTrees.size.toByte()).forEach { byteCodeStore.addByteCode(it) }

        registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        MoveOpecode.createByteCode(byteCodeStore.stackFrameSize.toByte(), registerAt).forEach { byteCodeStore.addByteCode(it) }
    }

    //endregion

    fun visit(blockStatement: BlockStatement, byteCodeStore: ByteCodeStore) {
        val astTrees = blockStatement.astTrees
        if (astTrees.isEmpty()) {
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
            BConstOpecode.createByteCode(0, registerAt).forEach { byteCodeStore.addByteCode(it) }
        } else {
            val initRegisterPosition = byteCodeStore.registerPosition
            astTrees.forEach {
                // blockの最後の値しか返り値として必要ないので、初期化している
                byteCodeStore.registerPosition = initRegisterPosition
                it.accept(this, byteCodeStore)
            }
        }
    }

    fun visit(defStatement: DefStatement, byteCodeStore: ByteCodeStore) {
        val blockStatement = defStatement.blockStatement
        val idNamesInDefSize = defStatement.idNamesInDefSize ?: throw OmuretuException("failed to search idnames size in def $this")

        byteCodeStore.apply {
            setRegisterAt(0)
            stackFrameSize = idNamesInDefSize + OmuretuVirtualMachine.SAVE_AREA_SIZE

            SaveOpecode.createByteCode(idNamesInDefSize.toByte()).forEach { byteCodeStore.addByteCode(it) }

            blockStatement.accept(this@CompileVisitor, byteCodeStore)

            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.registerPosition - 1)
            MoveOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) }

            RestoreOpecode.createByteCode(idNamesInDefSize.toByte()).forEach { byteCodeStore.addByteCode(it) }

            ReturnOpecode.createByteCode().forEach { byteCodeStore.addByteCode(it) }
        }
    }

    fun visit(ifStatement: IfStatement, byteCodeStore: ByteCodeStore) {
        val (condition, thenBlock, elseBlock) = ifStatement
        condition.accept(this, byteCodeStore)
        // elseが始まる位置を格納しているCodePosition
        val codePositionStartIfZero = byteCodeStore.codePosition
        val codePositionSavingStartElseBlock = codePositionStartIfZero + IfZeroOpecode.SHORT_START
        val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        IfZeroOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでelse文が始まる位置を渡す

        val registerPosition = byteCodeStore.registerPosition
        thenBlock.accept(this, byteCodeStore)

        val codePositionStartGoto = byteCodeStore.codePosition
        val codePositionSavingEndElseBlock = byteCodeStore.codePosition + GotoOpecode.SHORT_START
        GotoOpecode.createByteCode(0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでelse文が終わる位置を渡す

        (byteCodeStore.codePosition - codePositionStartIfZero).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode(codePositionSavingStartElseBlock + index, byte)
        }

        byteCodeStore.registerPosition = registerPosition
        elseBlock?.accept(this, byteCodeStore) ?: run {
            // TODO 何をしているのか調査
            val registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
            BConstOpecode.createByteCode(0, registerAt).forEach { byteCodeStore.addByteCode(it) }
        }

        (byteCodeStore.codePosition - codePositionStartGoto).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode(codePositionSavingEndElseBlock + index, byte)
        }
    }

    fun visit(whileStatement: WhileStatement, byteCodeStore: ByteCodeStore) {
        val (condition, body) = whileStatement
        val registerPosition = byteCodeStore.registerPosition

        var registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.nextRegister())
        BConstOpecode.createByteCode(0, registerAt)

        val codePositionStartCondition = byteCodeStore.codePosition
        condition.accept(this, byteCodeStore)

        val codePositionStartIfZero = byteCodeStore.codePosition
        registerAt = OmuretuVirtualMachine.encodeRegisterIndex(byteCodeStore.prevRegister())
        IfZeroOpecode.createByteCode(registerAt, 0).forEach { byteCodeStore.addByteCode(it) } // 0を渡しているがあとでbody文が終わる位置を渡す

        byteCodeStore.registerPosition = registerPosition
        body.accept(this, byteCodeStore)

        GotoOpecode.createByteCode((codePositionStartCondition - byteCodeStore.codePosition).toShort()).forEach { byteCodeStore.addByteCode(it) }
        (byteCodeStore.codePosition - codePositionStartIfZero).toShort().sliceByByte().forEachIndexed { index, byte ->
            byteCodeStore.setByteCode((codePositionStartIfZero + IfZeroOpecode.SHORT_START) + index, byte)
        }
    }
}
