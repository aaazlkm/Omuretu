package parser

import parser.exception.ParseException
import lexer.token.Token
import parser.ast.*

/**
 * ASTTree関連のインスタンスを生成するFactoryクラス
 * 共通で処理することがあるのでこのクラスにまとめている
 * FIXME うまくない気がするので、修正する
 */
interface ASTTreeFactory {
    companion object {
        /** ASTTree関連クラスのインスタンスの生成は以下のメソッド名を用いて行う */
        const val FACTORY_METHOD_NAME = "newInstance"

        fun createInstance(): ASTTreeFactory {
            return object : ASTTreeFactory {
                override fun makeASTTree(arguments: Any): ASTTree {
                    (arguments as? Token)?.let { token ->
                        return ASTLeaf(token)
                    }

                    (arguments as? List<ASTTree>)?.let { astTrees ->
                        return if (astTrees.size == 1) {
                            astTrees[0]
                        } else {
                            ASTList(astTrees.toList())
                        }
                    }
                    throw ParseException("定義されていません")
                }
            }
        }

        // リフレクションを使用することで、カスタムで定義されたクラスのコンストラクタを取得する
        fun createInstance(clazz: Class<out ASTTree>, argumentTypes: Class<*>): ASTTreeFactory {
            val method = clazz.getMethod(FACTORY_METHOD_NAME, argumentTypes)
            return object : ASTTreeFactory {
                override fun makeASTTree(arguments: Any): ASTTree {
                    return method.invoke(null, arguments) as? ASTTree ?: throw ParseException("class $clazz")
                }
            }
        }
    }

    fun makeASTTree(arguments: Any): ASTTree
}