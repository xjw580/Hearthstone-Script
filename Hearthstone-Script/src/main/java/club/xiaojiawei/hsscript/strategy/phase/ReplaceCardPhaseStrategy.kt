package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.bean.LogThread
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.*
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MulliganStateEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator.changeCard
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.status.War.me
import club.xiaojiawei.status.War.player1
import club.xiaojiawei.status.War.player2
import club.xiaojiawei.status.War.rival

/**
 * 换牌阶段
 *
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
object ReplaceCardPhaseStrategy : AbstractPhaseStrategy() {

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.MULLIGAN_STATE && tagChangeEntity.value == MulliganStateEnum.INPUT.name) {
            val gameId = tagChangeEntity.entity
            if (me.gameId == gameId || (!rival.gameId.isBlank() && rival.gameId != gameId)) {
                cancelAllTask()
//                执行换牌策略
                (LogThread({
                    log.info { "1号玩家牌库数量：" + player1.deckArea.cards.size }
                    log.info { "2号玩家牌库数量：" + player2.deckArea.cards.size }
//                    畸变模式会导致开局动画增加
                    SystemUtil.delay(20000 + (if (ConfigUtil.getBoolean(ConfigEnum.DISTORTION)) 4500 else 0))
                    if (PauseStatus.isPause) return@LogThread
                    changeCard()
                }, "Change Card Thread").also { addTask(it) }).start()
            }
        } else if (tagChangeEntity.tag == TagEnum.NEXT_STEP && StepEnum.MAIN_READY.name == tagChangeEntity.value) {
            currentPhase = WarPhaseEnum.SPECIAL_EFFECT_TRIGGER
            return true
        }
        return false
    }

}
