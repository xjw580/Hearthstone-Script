package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.hsscriptbase.enums.StepEnum
import club.xiaojiawei.hsscriptbase.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.bean.ChangeCardThread
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.enums.MulliganStateEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator.changeCard

/**
 * 换牌阶段
 *
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
object ReplaceCardPhaseStrategy : AbstractPhaseStrategy() {

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag === TagEnum.MULLIGAN_STATE && tagChangeEntity.value == MulliganStateEnum.INPUT.name) {
            val gameId = tagChangeEntity.entity
            val me = war.me
            val rival = war.rival
            if (me.gameId == gameId || (rival.gameId.isNotBlank() && rival.gameId != gameId)) {
                cancelAllTask()
//                执行换牌策略
                (ChangeCardThread {
                    changeCard()
                }.also { addTask(it) }).start()
            }
        } else if (tagChangeEntity.tag == TagEnum.NEXT_STEP && StepEnum.MAIN_READY.name == tagChangeEntity.value) {
            war.currentPhase = WarPhaseEnum.SPECIAL_EFFECT_TRIGGER
            return true
        }
        return false
    }


}
