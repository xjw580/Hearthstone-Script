package club.xiaojiawei.hsscriptbase.bean

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.util.isFalse

/**
 * @author 肖嘉威
 * @date 2024/9/8 17:02
 */
class LRunnable(private val ignoreInterrupted: Boolean = false, private var task: Runnable?) : Runnable {

    override fun run() {
        try {
            task?.run()
        } catch (e: Exception) {
            if (e is InterruptedException) {
                ignoreInterrupted.isFalse {
                    log.warn(e) { "操作中断" }
                }
            } else {
                log.error(e) { "发生错误" }
            }
        }
    }

}