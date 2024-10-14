package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.utils.PowerLogUtil
import club.xiaojiawei.status.War
import java.util.concurrent.TimeUnit

/**
 * 对局日志监听器
 * @author 肖嘉威
 * @date 2023/7/5 20:40
 */
object PowerLogListener :
    AbstractLogListener("Power.log", 0, 1000L, TimeUnit.MILLISECONDS) {

    override fun dealOldLog() {
        innerLogFile?.let {
            it.seek(it.length())
        }
        WarEx.reset()
    }

    override fun dealNewLog() {
        while (!PauseStatus.isPause && !AbstractPhaseStrategy.dealing) {
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
        when (War.currentPhase) {
            WarPhaseEnum.FILL_DECK -> {
                War.startTime = System.currentTimeMillis()
                WarPhaseEnum.FILL_DECK.phaseStrategy?.deal(line)
            }

            WarPhaseEnum.GAME_OVER -> {
                War.endTime = if (War.startTime == 0L) 0 else System.currentTimeMillis()
                WarPhaseEnum.GAME_OVER.phaseStrategy?.deal(line)
                WarEx.reset()
            }

            else -> War.currentPhase.phaseStrategy?.deal(line)
        }
        if (War.currentTurnStep == StepEnum.FINAL_GAMEOVER) {
            War.currentPhase = WarPhaseEnum.GAME_OVER
        }
    }

}