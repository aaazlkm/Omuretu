package ast

import parser.ast.ASTLeaf
import lexer.token.IdToken

class Name(
        override val token: IdToken
) : ASTLeaf(token)