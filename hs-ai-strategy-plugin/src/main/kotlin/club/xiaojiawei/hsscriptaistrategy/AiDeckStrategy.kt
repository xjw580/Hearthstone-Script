package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptaistrategy.action.ActionExecutor
import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptaistrategy.llm.LlmClient
import club.xiaojiawei.hsscriptaistrategy.prompt.ActionParser
import club.xiaojiawei.hsscriptaistrategy.prompt.GameStateSerializer
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
        val toRemove = cards.toList().filter { it.cost >= 4 }
        toRemove.forEach { cards.remove(it) }
    }

    override fun executeOutCard() {
        if (!AiConfig.isEnabled()) {
            return
        }
        val me = WAR.me
        if (!me.isValid()) {
            return
        }
        val rival = WAR.rival
        if (!rival.isValid()) {
            return
        }
        var actionCount = 0
        val maxActions = 20
        while (actionCount < maxActions) {
            val stateJson = try {
                GameStateSerializer.serialize()
            } catch (e: Exception) {
                log.error { "序列化场面失败: ${e.message}" }
                break
            }
            val messages = PromptBuilder.build(stateJson)
            val response = try {
                LlmClient.chat(messages)
            } catch (e: Exception) {
                log.error { "调用LLM失败: ${e.message}" }
                break
            }
            val action = ActionParser.parse(response) ?: break
            if (action.action == "end_turn") {
                log.info { "AI选择结束回合" }
                break
            }
            val success = ActionExecutor.execute(action, me, rival)
            if (!success) {
                log.warn { "AI动作执行失败: ${action.action}, 思考: ${action.thinking}" }
            }
            try {
                Thread.sleep(1500)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            actionCount++
            if (!me.isValid() || !rival.isValid()) {
                break
            }
        }
        log.info { "AI回合结束, 共执行 $actionCount 个动作" }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = 0

}
