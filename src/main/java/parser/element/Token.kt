package parser.element

// TODO あとで下記を消す
// check ok

// 拡張はしないがSkipと同じ粒度にするために継承
class Token constructor(vararg tokens: String) : Leaf(*tokens)
