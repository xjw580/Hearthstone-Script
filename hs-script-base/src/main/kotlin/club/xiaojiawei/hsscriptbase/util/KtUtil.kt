package club.xiaojiawei.hsscriptbase.util

import kotlin.random.Random

/**
 * @author 肖嘉威
 * @date 2024/9/28 14:18
 */

inline fun Boolean.isTrue(block: () -> Unit): Boolean {
    if (this) {
        block()
    }
    return this
}

inline fun Boolean.isFalse(block: () -> Unit): Boolean {
    if (!this) {
        block()
    }
    return this
}

fun <T> List<T>.randomSelectOrNull(): T? {
    if (this.isEmpty()) return null
    return this[Random.nextInt(this.size)]
}

fun <T> List<T>.randomSelect(): T {
    return this[Random.nextInt(this.size)]
}

fun <T> List<T>.randomMultiSelect(size: Int): List<T> {
    if (size <= 0) return emptyList()
    if (size >= this.size) {
        return this.shuffled()
    }
    return this.shuffled().take(size)
}


inline fun <T1, T2> withNotNull(a: T1?, b: T2?, block: (T1, T2) -> Unit): Boolean {
    return if (a != null && b != null) {
        block(a, b)
        true
    } else {
        false
    }
}