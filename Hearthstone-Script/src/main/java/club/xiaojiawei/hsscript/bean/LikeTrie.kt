package club.xiaojiawei.hsscript.bean

/**
 * # Trie树
 * @author 肖嘉威
 * @date 2024/11/12 9:32
 */
class LikeTrie<V> {

    private val root = TrieNode<V>()

    inner class TrieNode<V> {
        val children = mutableMapOf<Char, TrieNode<V>>()
        val regPattern = mutableListOf<Reg<V>>()
        var value: V? = null
    }

    inner class Reg<V>(val pattern: String, val value: V)

    operator fun set(key: String, value: V) {
        var currentNode = root
        val lowercaseKey = key.lowercase()
        var charKey = lowercaseKey.toCharArray()
        charKey.forEachIndexed { index, c ->
            currentNode = currentNode.children.getOrPut(c) { TrieNode() }
            if (c == '%') {
                currentNode.regPattern.add(Reg(lowercaseKey, value))
                return@forEachIndexed
            }
        }
        currentNode.value = value
    }

    /**
     * 通配符%表示0至2个字符（26个小写字母加10个阿拉伯数字）
     */
    operator fun get(pattern: String): V? = getHelper(root, pattern.lowercase(), 0)

    private fun getHelper(node: TrieNode<V>, str: String, index: Int): V? {
        if (index == str.length) {
            return node.value
        }

        val char = str[index]
        return when (char) {
            '%' -> {
                // 处理 % 通配符，尝试匹配 0 个字符或者多个字符
                // 1. 尝试继续匹配下一个字符，表示匹配零个字符
                val result = getHelper(node, str, index + 1)
                if (result != null) return result

                // 2. 尝试匹配下一个子节点，表示匹配一个字符后再继续
                for ((_, childNode) in node.children) {
                    getHelper(childNode, str, index)?.let { return it }
                }
                null
            }

//            '_' -> {
//                // _ 匹配任意单个字符
//                for ((_, childNode) in node.children) {
//                    getHelper(childNode, pattern, index + 1)?.let { return it }
//                }
//                null
//            }

            else -> {
                // 普通字符匹配
                (node.children[char]?.let { childNode ->
                    getHelper(childNode, str, index + 1)
                }) ?: let {
                    node.children['%']?.let { childNode ->
                        for (reg in childNode.regPattern) {
                            if (Regex(likeToRegex(reg.pattern)).matches(str)) {
                                return reg.value
                            }
                        }
                        null
                    }
                }
            }
        }
    }

    private fun likeToRegex(pattern: String): String {
        return pattern.replace("%", ".*")
    }
}