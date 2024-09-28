package club.xiaojiawei.utils

import club.xiaojiawei.bean.ReadableThread
import club.xiaojiawei.bean.WritableThread
import club.xiaojiawei.config.log
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2024/9/28 14:36
 */

fun onlyWriteRun(block: () -> Unit) {
    (Thread.currentThread() is WritableThread).isTrue { block() }.isFalse{
        log.error{"Only allowed to execute within the write thread"}
    }
}

fun onlyReadRun(block: () -> Unit) {
    (Thread.currentThread() is ReadableThread).isTrue { block() }.isFalse{
        log.error{"Only allowed to execute within the read thread"}
    }
}