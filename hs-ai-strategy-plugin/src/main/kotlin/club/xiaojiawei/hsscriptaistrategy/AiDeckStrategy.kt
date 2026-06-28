package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptaistrategy.action.ActionExecutor
import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptaistrategy.llm.LlmClient
import club.xiaojiawei.hsscriptaistrategy.prompt.ActionParser
import club.xiaojiawei.hsscriptaistrategy.prompt.GameStateSerializer
import club.xiaojiawei.hsscriptaistrategy.prompt.LlmAction
import club.xiaojiawei.hsscriptaistrategy.prompt.PromptBuilder
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.isValid
import club.xiaojiawei.hsscriptcardsdk.status.WAR
import club.xiaojiawei.hsscriptstrategysdk.DeckStrategy

class AiDeckStrategy : DeckStrategy() {

    override fun name(): String = "AI决策策略"

    override fun description(): String =
        "由大语言模型分析场面并决定每步动作（出牌、攻击、英雄技能、结束回合），需在配置中启用AI并填写LLM API信息"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "ai-strategy-llm-v1"

    override fun executeChangeCard(cards: HashSet<Card>) {
        log.info { "AI换牌开始, 手牌数: ${cards.size}" }
        try {
            val toRemove = cards.toList().filter { it.cost >= 4 }
            log.info { "AI换牌: 欲移除 ${toRemove.size} 张(费用>=4), 保留 ${cards.size - toRemove.size} 张" }
            toRemove.forEach { cards.remove(it) }
        } catch (e: Exception) {
            log.error { "AI换牌异常: ${e.message}" }
            e.printStackTrace()
        }
        log.info { "AI换牌结束, 剩余手牌数: ${cards.size}" }
    }

    override fun executeOutCard() {
        log.info { "AI出牌回合开始" }
        if (!AiConfig.isEnabled()) {
            log.warn { "AI未启用(AI_ENABLED=false), 跳过" }
            return
        }
        try {
            val me = WAR.me
            if (!me.isValid()) {
                log.warn { "我方玩家无效, 跳过" }
                return
            }
            val rival = WAR.rival
            if (!rival.isValid()) {
                log.warn { "敌方玩家无效, 跳过" }
                return
            }
            var actionCount = 0
            val maxActions = 20
            var consecutiveFailures = 0
            val maxConsecutiveFailures = 3
            var failureFeedback: String? = null
            var actionQueue: List<LlmAction> = emptyList()
            var queueIndex = 0

            while (actionCount < maxActions) {
                if (queueIndex >= actionQueue.size || failureFeedback != null) {
                    val stateJson = try {
                        GameStateSerializer.serialize()
                    } catch (e: Exception) {
                        log.error { "序列化场面失败: ${e.message}" }
                        break
                    }
                    val messages = PromptBuilder.build(stateJson, failureFeedback)
                    val response = try {
                        LlmClient.chat(messages)
                    } catch (e: Exception) {
                        log.error { "调用LLM失败: ${e.message}" }
                        break
                    }
                    actionQueue = ActionParser.parseActions(response) ?: break
                    if (actionQueue.isEmpty()) break
                    queueIndex = 0
                    failureFeedback = null
                    log.info { "AI规划了${actionQueue.size}个动作" }
                }

                val action = actionQueue[queueIndex]
                if (action.action == "end_turn") {
                    log.info { "AI选择结束回合" }
                    break
                }

                val handBefore = me.handArea.cards.size
                val myBoardBefore = me.playArea.cards.size
                val rivalBoardBefore = rival.playArea.cards.size

                val success = ActionExecutor.execute(action, me, rival)
                actionCount++
                queueIndex++

                if (!success) {
                    consecutiveFailures++
                    failureFeedback = "动作[${action.action}](card_index=${action.cardIndex}," +
                        "attacker_index=${action.attackerIndex},target_index=${action.targetIndex})执行失败。" +
                        "请勿重试相同动作，基于当前场面重新规划。"
                    log.warn { "AI动作失败(${consecutiveFailures}/${maxConsecutiveFailures}): ${action.action}" }
                    if (consecutiveFailures >= maxConsecutiveFailures) {
                        log.warn { "连续失败${maxConsecutiveFailures}次，结束回合" }
                        break
                    }
                    continue
                }

                try {
                    Thread.sleep(800)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }

                val handAfter = me.handArea.cards.size
                val myBoardAfter = me.playArea.cards.size
                val rivalBoardAfter = rival.playArea.cards.size
                if (handAfter != handBefore || myBoardAfter != myBoardBefore || rivalBoardAfter != rivalBoardBefore) {
                    log.info { "场面变化(手牌:$handBefore->$handAfter,我方:$myBoardBefore->$myBoardAfter,敌方:$rivalBoardBefore->$rivalBoardAfter)，重新评估" }
                    actionQueue = emptyList()
                    queueIndex = 0
                }

                if (!me.isValid() || !rival.isValid()) break
            }
            log.info { "AI回合结束, 共执行 $actionCount 个动作" }
        } catch (e: Exception) {
            log.error { "AI出牌回合异常: ${e.message}" }
            e.printStackTrace()
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        log.info { "AI发现选牌, 候选数: ${cards.size}" }
        return try {
            0
        } catch (e: Exception) {
            log.error { "AI发现选牌异常: ${e.message}" }
            0
        }
    }

}
