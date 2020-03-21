package parser.ast

import omuretu.environment.Environment
import omuretu.NestedIdNameLocationMap

interface ASTTree {

    fun lookupIdNamesLocation(idNameLocationMap: NestedIdNameLocationMap)

    fun evaluate(environment: Environment): Any

    /**
     * デバック用
     *
     * @return String
     */
    override fun toString(): String
}
