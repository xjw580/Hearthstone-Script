package club.xiaojiawei.util

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