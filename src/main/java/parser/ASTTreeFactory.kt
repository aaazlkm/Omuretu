package parser

import exception.ParseException
import lexer.token.Token
import parser.ast.ASTLeaf
import parser.ast.ASTList
import parser.ast.ASTTree
import parser.ast.AstListWithChildren

/**
 * ASTTree関連のインスタンスを生成するFactoryクラス
 * 共通で処理することがあるのでこのクラスにまとめている
 */
abstract class ASTTreeFactory private constructor() {
    companion object {
        fun createInstance(): ASTTreeFactory {
            return object : ASTTreeFactory() {
                override fun make(vararg constructorArguments: Any): ASTTree {
                    // TODO　うまくない気がするので修正する
                    return when (constructorArguments) {
                        is Token -> ASTLeaf(constructorArguments)
                        is List<*> -> {
                            val astTrees = constructorArguments as? List<ASTTree> ?: throw ParseException("キャストに失敗しました")
                            if (astTrees.size == 1) {
                                astTrees[0]
                            } else {
                                ASTList(astTrees)
                            }
                        }
                        else -> throw ParseException("定義されていません")
                    }
                }
            }
        }

        // リフレクションを使用することで、カスタムで定義されたコンストラクタを取得する
        fun createInstance(clazz: Class<out ASTTree>, vararg constructorArgumentTypes: Class<*>): ASTTreeFactory {
            return if (clazz is AstListWithChildren) {
                object : ASTTreeFactory() {
                    override fun make(vararg constructorArguments: Any): ASTTree {
                        val astTrees = constructorArguments as? List<ASTTree>  ?: throw ParseException("")
                        return clazz.make(astTrees)
                    }
                }
            } else {
                // TODO 動作確認
                val constructor = clazz.getConstructor(*constructorArgumentTypes)
                object : ASTTreeFactory() {
                    override fun make(vararg constructorArguments: Any): ASTTree {
                        return constructor.newInstance(constructorArguments)
                    }
                }
            }
        }
    }

    abstract fun make(vararg constructorArguments: Any): ASTTree
}