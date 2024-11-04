package club.xiaojiawei.bean

import club.xiaojiawei.config.log

/**
 * @author 肖嘉威
 * @date 2024/9/8 17:02
 */
class LogRunnable(private var task: Runnable?) : Runnable {

    override fun run() {
        try {
            task?.run()
        } catch (e: Exception) {
            if (e is InterruptedException) {
                log.debug(e) { "Runnable发生错误" }
            } else {
                log.error(e) { "Runnable发生错误" }
            }
        }
    }

}