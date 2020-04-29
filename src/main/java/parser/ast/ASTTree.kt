package parser.ast

import omuretu.environment.base.VariableEnvironment
import omuretu.environment.NestedIdNameLocationMap
import omuretu.environment.base.TypeEnvironment
import omuretu.typechecker.Type
import omuretu.vertualmachine.ByteCodeStore

interface ASTTree {

    fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap)

    fun checkType(typeEnvironment: TypeEnvironment): Type

    fun compile(byteCodeStore: ByteCodeStore) { }

    fun evaluate(variableEnvironment: VariableEnvironment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
