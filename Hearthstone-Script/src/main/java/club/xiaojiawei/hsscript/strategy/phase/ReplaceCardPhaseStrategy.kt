package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.bean.LThread
import club.xiaojiawei.enums.*
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.enums.MulliganStateEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator.changeCard
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.status.War.me
import club.xiaojiawei.status.War.rival

/**
 * 换牌阶段
 *
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
object ReplaceCardPhaseStrategy : AbstractPhaseStrategy(){

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.MULLIGAN_STATE && tagChangeEntity.value == MulliganStateEnum.INPUT.name) {
            val gameId = tagChangeEntity.entity
            if (me.gameId == gameId || (!rival.gameId.isBlank() && rival.gameId != gameId)) {
                cancelAllTask()
//                执行换牌策略
                (LThread({
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
