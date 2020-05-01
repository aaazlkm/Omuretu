package parser.ast

import omuretu.environment.IdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.environment.base.VariableEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore
import omuretu.visitor.CheckTypeVisitor
import omuretu.visitor.CompileVisitor
import omuretu.visitor.EvaluateVisitor
import omuretu.visitor.IdNameLocationVisitor

interface ASTTree {

    fun accept(idNameLocationVisitor: IdNameLocationVisitor, idNameLocationMap: IdNameLocationMap)

    fun accept(checkTypeVisitor: CheckTypeVisitor, typeEnvironment: TypeEnvironment): Type

    fun accept(compileVisitor: CompileVisitor, byteCodeStore: ByteCodeStore) {}

    fun accept(evaluateVisitor: EvaluateVisitor, variableEnvironment: VariableEnvironment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
