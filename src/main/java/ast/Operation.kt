package ast

import parser.ast.ASTLeaf
import lexer.token.IdToken

class Operation(
        override val token: IdToken
) : ASTLeaf(token)