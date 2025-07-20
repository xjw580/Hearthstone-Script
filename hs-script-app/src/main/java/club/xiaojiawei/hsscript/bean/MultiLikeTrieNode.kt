package club.xiaojiawei.hsscript.bean

import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/1/22 19:35
 */
class MultiLikeTrieNode<V>(
    val ignoreCase: Boolean = true,
    var parent: MultiLikeTrieNode<V>? = null,
    val c: Char? = null
) {


    val values: MutableSet<V> = mutableSetOf()

    val children: MutableMap<Char, MultiLikeTrieNode<V>> = mutableMapOf()

    operator fun get(s: String): List<V> {
        return find(s, false)?.values?.toList() ?: emptyList()
    }

    operator fun set(s: String, value: V) {
        find(s, true)?.let { node ->
            var tempMultiLikeTrieNode: MultiLikeTrieNode<V>? = node
            while (tempMultiLikeTrieNode != null) {
                tempMultiLikeTrieNode.values.add(value)
                tempMultiLikeTrieNode = tempMultiLikeTrieNode.parent
            }
        }
    }

    private fun find(s: String, create: Boolean): MultiLikeTrieNode<V>? {
        var currentMultiLikeTrieNode: MultiLikeTrieNode<V>? = this
        var currentChild: MutableMap<Char, MultiLikeTrieNode<V>> = children
        val text = if (ignoreCase) s.lowercase(Locale.getDefault()) else s
        for (c in text) {
            currentChild[c]?.let { node ->
                currentMultiLikeTrieNode = node
                currentChild = node.children
            } ?: let {
                if (create) {
                    val newMultiLikeTrieNode =
                        MultiLikeTrieNode(ignoreCase = this.ignoreCase, parent = currentMultiLikeTrieNode, c = c)
                    currentChild[c] = newMultiLikeTrieNode
                    currentMultiLikeTrieNode = newMultiLikeTrieNode
                    currentChild = newMultiLikeTrieNode.children
                } else {
                    return null
                }
            }
        }
        return currentMultiLikeTrieNode
    }
}