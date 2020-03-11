package parser.ast

import lexer.token.Token

open class ASTLeaf(open val token: Token) : ASTTree