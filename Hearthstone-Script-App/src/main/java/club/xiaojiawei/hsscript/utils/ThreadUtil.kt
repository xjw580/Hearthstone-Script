package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.bean.ReadableThread
import club.xiaojiawei.bean.WritableThread
import club.xiaojiawei.config.log
import javafx.application.Platform

/**
 * @author 肖嘉威
 * @date 2024/9/28 14:36
 */

fun onlyWriteRun(block: () -> Unit) {
    if ((Thread.currentThread() is WritableThread) || Platform.isFxApplicationThread()) {
        block()
    } else {
        log.error { "Only allowed to execute within the write thread" }
    }
}

fun onlyReadRun(block: () -> Unit) {
    if ((Thread.currentThread() is ReadableThread) || Platform.isFxApplicationThread()) {
        block()
    } else {
        log.error { "Only allowed to execute within the read thread" }
    }
}

fun go(name: String? = null, block: () -> Unit) {
    val ofVirtual = Thread.ofVirtual()
    name?.let {
        ofVirtual.name(name)
    }
    ofVirtual.start(LogRunnable { block })
}