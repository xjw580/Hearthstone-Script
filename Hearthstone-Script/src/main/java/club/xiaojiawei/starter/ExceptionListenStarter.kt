package club.xiaojiawei.starter

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.LISTEN_LOG_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.core.Core
import club.xiaojiawei.status.PauseStatus
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import javafx.beans.value.ObservableValue
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * @author 肖嘉威
 * @date 2024/9/28 21:58
 */
object ExceptionListenStarter : AbstractStarter() {

    @Volatile
    var lastActiveTime: Long = 0

    private var errorScheduledFuture: ScheduledFuture<*>? = null

    private const val MAX_IDLE_TIME = 10 * 60 * 1000L

    init {
        PauseStatus.addListener{ _: ObservableValue<out Boolean>?, _: Boolean?, newValue: Boolean ->
            newValue.isTrue {
                closeListener()
            }
        }
    }

    private fun closeListener() {
        errorScheduledFuture?.let {
            it.isDone.isFalse {
                it.cancel(true)
                errorScheduledFuture = null
            }
        }
    }

    override fun execStart() {
        closeListener()
        log.info { "开始监听异常情况" }
        lastActiveTime = System.currentTimeMillis()
        errorScheduledFuture = LISTEN_LOG_THREAD_POOL.scheduleAtFixedRate(LogRunnable {
            if (!PauseStatus.isPause && System.currentTimeMillis() - lastActiveTime > MAX_IDLE_TIME
            ) {
                log.info { "监听到长时间无作为，准备重启游戏" }
                lastActiveTime = System.currentTimeMillis()
                Core.restart()
            }
        }, 0, 10, TimeUnit.MINUTES)
    }

}