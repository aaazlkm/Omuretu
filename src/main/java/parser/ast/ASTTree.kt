package parser.ast

import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap
import omuretu.vertualmachine.ByteCodeStore

interface ASTTree {

    fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap)

    fun compile(byteCodeStore: ByteCodeStore) { }

    fun evaluate(environment: Environment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
