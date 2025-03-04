package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.data.MAX_LOG_SIZE_B
import club.xiaojiawei.hsscript.data.MAX_LOG_SIZE_KB
import club.xiaojiawei.hsscript.initializer.BaseInitializer
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import club.xiaojiawei.status.WAR
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

/**
 * 对局日志监听器
 * @author 肖嘉威
 * @date 2023/7/5 20:40
 */
object PowerLogListener :
    AbstractLogListener("Power.log", 0, 1000L, TimeUnit.MILLISECONDS) {

    private val war = WAR

    private const val RESERVE_SIZE_B = 4 * 1024 * 1024

    override fun dealOldLog() {
        innerLogFile?.let {
            it.seek(it.length())
        }
        WarEx.reset()
    }

    override fun dealNewLog() {
        while (!PauseStatus.isPause && !AbstractPhaseStrategy.dealing && WorkListener.working) {
            innerLogFile?.let {
                val line = it.readLine()
                if (line == null) {
                    return@dealNewLog
                } else if (PowerLogUtil.isRelevance(line)) {
                    resolveLog(line)
                }
            } ?: return
        }
    }

    private fun resolveLog(line: String) {
        when (war.currentPhase) {
            WarPhaseEnum.FILL_DECK -> {
                WarPhaseEnum.FILL_DECK.phaseStrategy?.deal(line)
            }

            WarPhaseEnum.GAME_OVER -> {
                WarPhaseEnum.GAME_OVER.phaseStrategy?.deal(line)
            }

            else -> war.currentPhase.phaseStrategy?.deal(line)
        }
        if (war.currentTurnStep == StepEnum.FINAL_GAMEOVER) {
            war.currentPhase = WarPhaseEnum.GAME_OVER
        }
    }

    fun checkPowerLogSize(): Boolean {
        val logFile = logFile
        logFile ?: return false

        if (logFile.length() + RESERVE_SIZE_B >= MAX_LOG_SIZE_B) {
            log.info { "power.log即将达到" + (MAX_LOG_SIZE_KB) + "KB，准备重启游戏" }
            Core.restart()
            return false
        }
        return true
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        innerLogFile = RandomAccessFile("S:\\Hearthstone\\Logs\\Hearthstone_2024_12_04_19_06_41\\Power - 副本.log", "r")
        innerLogFile = RandomAccessFile("S:\\Hearthstone\\Logs\\Hearthstone_2024_11_27_17_38_36\\Power.log", "r")
        PauseStatus.isPause = false
        WorkListener.working = true
        BaseInitializer().init()
        WarEx.reset()
//        innerLogFile = RandomAccessFile("S:\\Hearthstone\\Logs\\Hearthstone_2024_12_04_19_06_41\\Power.log", "r")
        dealNewLog()
    }
}
