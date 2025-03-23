package club.xiaojiawei.hsscript.bean

import kotlin.math.max

/**
 * @author 肖嘉威
 * @date 2025/3/23 15:26
 */
class FixedSizeStack<T>(private val maxSize: Int) {

    private val stack = ArrayDeque<T>()

    fun push(item: T) {
        if (stack.size >= maxSize) {
            stack.removeFirst() // 移除最早的元素
        }
        stack.addLast(item)
    }

    fun pop(): T? {
        return if (stack.isNotEmpty()) stack.removeLast() else null
    }

    fun peek(): T? {
        return stack.lastOrNull()
    }

    fun peekList(size: Int): MutableList<T> {
        val end = max(0, stack.size - size)
        val res = mutableListOf<T>()
        for (i in stack.size - 1 downTo end) {
            res.add(stack[i])
        }
        return res
    }

    fun toMutableList(): MutableList<T> {
        return stack.toMutableList()
    }

    fun toList(): List<T> {
        return stack.toList()
    }

    fun isEmpty(): Boolean = stack.isEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()

    fun size(): Int = stack.size
}
