package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscriptbase.util.isTrue

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:21
 */
object WarTimeoutSurrenderService : Service<Int>() {
    override val isRunning: Boolean
        get() {
            return thread?.isAlive == true
        }

    private var thread: Thread? = null

    override fun execStart(): Boolean {
        thread =
            Thread {
                try {
                    while (thread?.isInterrupted == false) {
                        Thread.sleep(1000)
                        if (WarEx.inWar && WorkTimeListener.working && WarEx.war.startTime != 0L) {
                            val timeoutSec = ConfigUtil.getInt(ConfigEnum.WAR_TIMEOUT_SURRENDER)
                            if (System.currentTimeMillis() - WarEx.war.startTime > timeoutSec * 1000 && DeckStrategyManager.currentDeckStrategy?.needSurrender == false) {
                                DeckStrategyManager.currentDeckStrategy?.needSurrender = true
                                log.info { "触发游戏对局超时，超过${timeoutSec}秒，准备投降" }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (e !is InterruptedException) {
                        log.error(e) { "" }
                    }
                }
            }.apply {
                name = "WarTimeout Thread"
                start()
            }
        return true
    }

    override fun execStop(): Boolean {
        thread?.let {
            it.isAlive.isTrue {
                it.interrupt()
            }
            thread = null
        }
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean =
        (value ?: ConfigUtil.getInt(ConfigEnum.WAR_TIMEOUT_SURRENDER)) > 0
}
