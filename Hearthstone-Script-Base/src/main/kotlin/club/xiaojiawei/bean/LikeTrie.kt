package club.xiaojiawei.bean

import club.xiaojiawei.util.withNotNull

/**
 * # Trie树
 * @author 肖嘉威
 * @date 2024/11/12 9:32
 */
class LikeTrie<V>() {

    constructor(defaultValue: V) : this() {
        this.defaultValue = defaultValue
    }

    private var defaultValue: V? = null

    private val root = TrieNode<V>()

    class TrieNode<V> {
        val children = mutableMapOf<Char, TrieNode<V>>()
        val regPattern = mutableListOf<Reg<V>>()
        var key: String? = null
        var value: V? = null
    }

    class Entry<V>(
        var key: String,
        var value: V,
    )

    class Reg<V>(val pattern: String, val value: V)

    /**
     * 设置元素
     * @param key 键，通配符%表示任意个字符
     * @param value 值
     */
    operator fun set(key: String, value: V) {
        var currentNode = root
        val lowercaseKey = key.lowercase()
        val charKey = lowercaseKey.toCharArray()
        for (c in charKey) {
            currentNode = currentNode.children.getOrPut(c) { TrieNode() }
            if (c == '%') {
                currentNode.regPattern.add(Reg(lowercaseKey, value))
                break
            }
        }
        currentNode.value = value
        currentNode.key = key
    }

    /**
     * 获取元素集合
     */
    fun data(): MutableList<Entry<V>> {
        val data = mutableListOf<Entry<V>>()
        collectData(root, data)
        return data
    }

    private fun collectData(node: TrieNode<V>, data: MutableList<Entry<V>>) {
        withNotNull(node.key, node.value) { k, v ->
            data.add(Entry(k, v))
        }
        for (entry in node.children) {
            collectData(entry.value, data)
        }
    }

    /**
     * 清空元素
     */
    fun clear() {
        root.children.clear()
        root.key = null
        root.value = null
        root.regPattern.clear()
    }

    /**
     * 获取元素
     * @param key 键，通配符%表示任意个字符
     * @param defaultValue 默认值，找不到指定元素时返回此值
     */
    fun getOrDefault(key: String, defaultValue: V): V {
        return getHelper(root, key.lowercase(), 0) ?: defaultValue
    }

    /**
     * 获取元素
     * @param key 键，通配符%表示任意个字符
     * @param defaultValue 默认值，找不到指定元素时返回此值
     */
    fun getOrDefault(key: String, defaultValueExp: () -> V): V {
        return getHelper(root, key.lowercase(), 0) ?: defaultValueExp()
    }

    /**
     * 获取元素
     * @param key 键，通配符%表示任意个字符
     */
    operator fun get(key: String): V? = getHelper(root, key.lowercase(), 0) ?: this.defaultValue

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