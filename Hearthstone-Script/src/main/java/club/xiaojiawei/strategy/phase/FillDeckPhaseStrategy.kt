package club.xiaojiawei.strategy.phase

import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.enums.TagEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.status.DeckStrategyManager
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.status.War.startWar
import club.xiaojiawei.strategy.AbstractPhaseStrategy
import club.xiaojiawei.strategy.DeckStrategyActuator.deckStrategy

/**
 * 起始填充牌库阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
object FillDeckPhaseStrategy : AbstractPhaseStrategy() {

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.TURN && tagChangeEntity.value == "1") {
            currentPhase = WarPhaseEnum.DRAWN_INIT_CARD
            return true
        }
        return false
    }

    override fun dealOtherThenIsOver(line: String): Boolean {
        if (line.contains("CREATE_GAME")) {
            deckStrategy = DeckStrategyManager.CURRENT_DECK_STRATEGY.get()
            startWar(DeckStrategyManager.CURRENT_DECK_STRATEGY.get().runModes[0])
        }
        return super.dealOtherThenIsOver(line)
    }

}
