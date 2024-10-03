package club.xiaojiawei.strategy.phase

import club.xiaojiawei.bean.LogThread
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.TagEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.strategy.AbstractPhaseStrategy
import club.xiaojiawei.strategy.DeckStrategyActuator
import club.xiaojiawei.utils.SystemUtil

/**
 * 游戏回合阶段
 *
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
object GameTurnPhaseStrategy : AbstractPhaseStrategy() {

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.STEP) {
            if (tagChangeEntity.value == StepEnum.MAIN_ACTION.name) {
                if (War.me === War.currentPlayer && War.me.isValid()) {
                    log.info { "我方回合" }
                    cancelAllTask()
                    War.isMyTurn = true
                    // 异步执行出牌策略，以便监听出牌后的卡牌变动
                    (LogThread({
                        // 等待动画结束
                        SystemUtil.delay(4000)
                        DeckStrategyActuator.outCard()
                    }, "OutCard Thread").also { addTask(it) }).start()
                } else {
                    log.info { "对方回合" }
                    War.isMyTurn = false
                    cancelAllTask()
                }
            } else if (tagChangeEntity.value == StepEnum.MAIN_END.name) {
                War.isMyTurn = false
                cancelAllTask()
            }
        }
        return false
    }

}
