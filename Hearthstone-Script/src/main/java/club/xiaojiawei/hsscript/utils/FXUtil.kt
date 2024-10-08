package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import javafx.application.Platform

/**
 * @author 肖嘉威
 * @date 2024/9/28 14:22
 */

fun platformRunLater(block: () -> Unit) {
    Platform.isFxApplicationThread().isTrue {
        block()
    }.isFalse {
        Platform.runLater {
            block()
        }
    }
}