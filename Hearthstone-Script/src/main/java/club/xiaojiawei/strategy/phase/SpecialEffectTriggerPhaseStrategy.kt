package club.xiaojiawei.strategy.phase

import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.TagEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.strategy.AbstractPhaseStrategy
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component

/**
 * 特殊效果触发阶段（如开局的狼王、巴库、大主教等）
 * @author 肖嘉威
 * @date 2022/11/26 17:23
 */
object SpecialEffectTriggerPhaseStrategy : AbstractPhaseStrategy() {

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.STEP && tagChangeEntity.value == StepEnum.MAIN_READY.name) {
            currentPhase = WarPhaseEnum.GAME_TURN_PHASE
            return true
        }
        return false
    }

}
