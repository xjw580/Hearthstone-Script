package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.LISTEN_LOG_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.util.isFalse
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * @author 肖嘉威
 * @date 2024/9/28 21:58
 */
class ExceptionListenStarter : AbstractStarter() {

    private var errorScheduledFuture: ScheduledFuture<*>? = null

    init {
        TaskManager.addTask(this)
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
        Core.lastActiveTime = System.currentTimeMillis()
        errorScheduledFuture = LISTEN_LOG_THREAD_POOL.scheduleAtFixedRate(LRunnable {
            if (PauseStatus.isPause || !WorkListener.working) {
                closeListener()
                return@LRunnable
            }
            val idleTime = ConfigUtil.getLong(ConfigEnum.IDLE_MAXIMUM_TIME)
            if (System.currentTimeMillis() - Core.lastActiveTime > idleTime * 60_000L
            ) {
                Core.lastActiveTime = System.currentTimeMillis()
                log.info { "空闲时间超过${idleTime}分钟，准备重启游戏" }
                Core.restart()
            }
        }, 0, 1, TimeUnit.MINUTES)
    }

    override fun close() {
        super.close()
        closeListener()
    }

}