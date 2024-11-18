package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.bean.LThread
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator.deckStrategy
import club.xiaojiawei.status.War.currentPhase

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
            deckStrategy = DeckStrategyManager.currentDeckStrategy
            WarEx.startWar(DeckStrategyManager.currentDeckStrategy?.runModes[0])
            (LThread({
                DeckStrategyActuator.reset()
            }, "Reset Deck Strategy Thread").also { addTask(it) }).start()

        }
        return super.dealOtherThenIsOver(line)
    }

}
