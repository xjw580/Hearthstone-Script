package club.xiaojiawei.hsscript.strategy.phase

import club.xiaojiawei.bean.isValid
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.hsscript.bean.Behavior
import club.xiaojiawei.hsscript.bean.DeckStrategyThread
import club.xiaojiawei.hsscript.bean.PlayerBehavior
import club.xiaojiawei.hsscript.bean.log.Block
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.status.War
import club.xiaojiawei.util.isTrue

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
                    (DeckStrategyThread({
                        (ConfigUtil.getBoolean(ConfigEnum.RANDOM_EMOTION) && War.me.turn == 0).isTrue {
                            GameUtil.sendGreetEmoji()
                            SystemUtil.delayShortMedium()
                        }
                        DeckStrategyActuator.outCard()
                    }, "OutCard Thread").also { addTask(it) }).start()
                } else {
                    log.info { "对方回合" }
                    War.isMyTurn = false
                    cancelAllTask()
                    ConfigUtil.getBoolean(ConfigEnum.RANDOM_EMOTION).isTrue {
                        DeckStrategyActuator.randEmoji()
                    }
                    if (ConfigUtil.getBoolean(ConfigEnum.RANDOM_EVENT)) {
                        (DeckStrategyThread({
                            DeckStrategyActuator.randomDoSomething()
                        }, "Random Do Something Thread").also { addTask(it) }).start()
                    }
                }
            } else if (tagChangeEntity.value == StepEnum.MAIN_END.name) {
                War.isMyTurn = false
                cancelAllTask()
            }
        }
        return false
    }


    private var rivalBehavior: PlayerBehavior? = null

    private fun addBehavior(behavior: Behavior) {
        val currentPlayer = War.currentPlayer
        if (rivalBehavior == null && War.rival == currentPlayer) {
            rivalBehavior = PlayerBehavior(currentPlayer)
        }
        if (currentPlayer == War.rival) {
            rivalBehavior?.let {
                val behaviors = it.behaviors
                behaviors.add(behavior)
                if (behaviors.size > 100) {
                    behaviors.removeFirst()
                }
            }
        }
    }

    override fun dealBlockIsOver(line: String, block: Block): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.ONLY_ROBOT)) {
            if (block.blockType == BlockTypeEnum.ATTACK || block.blockType == BlockTypeEnum.PLAY) {
                addBehavior(Behavior(block.blockType))
                rivalBehavior?.let {
//                    val robotPlayerProbability = RobotPlayerUtil.calcRobotPlayerProbability(it)
//                    if (robotPlayerProbability < 0.5) {
//                        log.info { "发现对面这个b疑似真人，准备投降，gameId:${War.rival.gameId}" }
//                    }
                }
            }
        }
        return false
    }
}
