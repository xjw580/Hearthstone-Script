package club.xiaojiawei.util

import kotlin.random.Random

/**
 * @author 肖嘉威
 * @date 2024/9/28 14:18
 */

fun Boolean.isTrue(block: () -> Unit): Boolean {
    if (this) {
        block()
    }
    return this
}

fun Boolean.isFalse(block: () -> Unit): Boolean {
    if (!this) {
        block()
    }
    return this
}

fun <T> List<T>.randomSelect(): T {
    return this[Random.nextInt(this.size)]
}