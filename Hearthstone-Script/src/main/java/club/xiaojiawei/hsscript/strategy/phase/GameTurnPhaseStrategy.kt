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

    private fun checkRivalRobot() {
        log.info { "计算对面" }
        if (rivalBehavior.renewCalcRobotProbability() < 0.1) {
            log.info { "发现对面这个b疑似真人，准备投降，gameId:${War.rival.gameId}" }
        }
        log.info { "对面是脚本概率:" + rivalBehavior.robotProbability }
    }

    private fun checkMeRobot() {
        log.info { "计算我方" }
        if (meBehavior.renewCalcRobotProbability() < 0.1) {
            log.info { "发现自己这个b疑似真人，准备投降，gameId:${War.me.gameId}" }
        }
        log.info { "我是脚本概率:" + meBehavior.robotProbability }
    }

    override fun dealTagChangeThenIsOver(line: String, tagChangeEntity: TagChangeEntity): Boolean {
        if (tagChangeEntity.tag == TagEnum.STEP) {
            if (tagChangeEntity.value == StepEnum.MAIN_ACTION.name) {
                if (War.me === War.currentPlayer && War.me.isValid()) {
                    log.info { "我方回合" }
                    cancelAllTask()
                    War.isMyTurn = true
                    if (ConfigUtil.getBoolean(ConfigEnum.ONLY_ROBOT)) {
                        checkRivalRobot()
                        meBehavior.behaviors.clear()
                    }
                    // 异步执行出牌策略，以便监听出牌后的卡牌变动
                    (DeckStrategyThread({
                        (ConfigUtil.getBoolean(ConfigEnum.RANDOM_EMOTION) && War.me.turn == 0).isTrue {
                            GameUtil.sendGreetEmoji()
                            SystemUtil.delayShortMedium()
                        }
                        DeckStrategyActuator.outCard()
                        if (ConfigUtil.getBoolean(ConfigEnum.ONLY_ROBOT)) {
                            checkMeRobot()
                        }
                    }, "OutCard Thread").also { addTask(it) }).start()
                } else {
                    log.info { "对方回合" }
                    War.isMyTurn = false
                    cancelAllTask()
                    if (ConfigUtil.getBoolean(ConfigEnum.ONLY_ROBOT)) {
                        rivalBehavior.behaviors.clear()
                    }
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


    private val rivalBehavior by lazy {
        PlayerBehavior(War.rival)
    }

    private val meBehavior by lazy {
        PlayerBehavior(War.me)
    }

    override fun dealBlockIsOver(line: String, block: Block): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.ONLY_ROBOT)) {
            if (block.blockType == BlockTypeEnum.ATTACK || block.blockType == BlockTypeEnum.PLAY) {
                val behavior = Behavior(block.blockType)
                if (War.currentPlayer == War.me) {
                    meBehavior.behaviors.add(behavior)
                } else if (War.currentPlayer == War.rival) {
                    rivalBehavior.behaviors.add(behavior)
                }
            }
        }
        return false
    }
}
