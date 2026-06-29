package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptaistrategy.action.ActionExecutor
import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptaistrategy.llm.ChatMessage
import club.xiaojiawei.hsscriptaistrategy.llm.LlmClient
import club.xiaojiawei.hsscriptaistrategy.prompt.ActionParser
import club.xiaojiawei.hsscriptaistrategy.prompt.GameStateSerializer
import club.xiaojiawei.hsscriptaistrategy.prompt.LlmAction
import club.xiaojiawei.hsscriptaistrategy.prompt.PromptBuilder
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
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
                        log.info { "LLM调用失败，本回合切换到简易激进策略" }
                        fallbackExecute(me, rival)
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
                    Thread.sleep(1200)
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
        if (cards.isEmpty()) return 0
        if (!AiConfig.isEnabled()) return 0
        if (LlmClient.lastResponseTime > 20000) {
            log.info { "LLM响应较慢(${LlmClient.lastResponseTime}ms)，发现选牌用简易策略(选第一张)" }
            return 0
        }
        return try {
            val options = cards.mapIndexed { i, c ->
                "[$i] ${c.entityName} cost=${c.cost} type=${c.cardType.name}" +
                    if (c.atc > 0 || c.health > 0) " atk=${c.atc} hp=${c.health}" else ""
            }.joinToString("\n")
            val mana = WAR.me.usableResource
            val messages = listOf(
                ChatMessage("system", "你是炉石传说AI。从发现选项中选最优的一张，只回复数字。"),
                ChatMessage("user", "可用法力:$mana\n选项:\n$options\n回复下标(0-${cards.size - 1})："),
            )
            val response = LlmClient.chat(messages)
            val index = response.trim().filter { it.isDigit() }.toIntOrNull() ?: 0
            val clamped = index.coerceIn(0, cards.size - 1)
            log.info { "AI发现选牌: 下标$clamped, ${cards[clamped].entityName}" }
            clamped
        } catch (e: Exception) {
            log.warn { "AI发现选牌失败，选第一张: ${e.message}" }
            0
        }
    }

    private fun fallbackExecute(me: Player, rival: Player) {
        try {
            val fallbackId = AiConfig.fallbackStrategyId()
            val cl = Thread.currentThread().contextClassLoader ?: DeckStrategy::class.java.classLoader
            val strategies = java.util.ServiceLoader.load(DeckStrategy::class.java, cl).toList()
            log.info { "ServiceLoader找到${strategies.size}个策略" }
            strategies.forEach { log.info { "  策略: id=${it.id()}, name=${it.name()}" } }
            val radicalStrategy = strategies.firstOrNull { it.id() == fallbackId }
            if (radicalStrategy != null) {
                log.info { "使用激进策略($fallbackId)兜底" }
                radicalStrategy.reset()
                radicalStrategy.executeOutCard()
                log.info { "激进策略兜底完毕" }
                return
            }
            val className = "club.xiaojiawei.hsscriptbasestrategy.strategy.HsRadicalDeckStrategy"
            val radicalStrategy2 = try {
                Class.forName(className, true, cl)?.getDeclaredConstructor()?.newInstance() as? DeckStrategy
            } catch (e: Exception) {
                log.warn { "反射加载激进策略失败: ${e.message}" }
                null
            }
            if (radicalStrategy2 != null) {
                log.info { "使用激进策略(反射)兜底" }
                radicalStrategy2.reset()
                radicalStrategy2.executeOutCard()
                log.info { "激进策略(反射)兜底完毕" }
                return
            }
            log.warn { "未找到激进策略($fallbackId)，使用简易兜底" }
            simpleFallback(me, rival)
        } catch (e: Exception) {
            log.error { "兜底策略异常: ${e.message}" }
            simpleFallback(me, rival)
        }
    }

    private fun simpleFallback(me: Player, rival: Player) {
        try {
            log.info { "简易兜底开始" }
            val hand = me.handArea.cards.toList().sortedBy { it.cost }
            for (card in hand) {
                if (me.usableResource < card.cost) continue
                if (card.action.power() != null) {
                    log.info { "兜底出牌: ${card.entityName}(${card.cost}费)" }
                    Thread.sleep(1000)
                }
            }
            val myBoard = me.playArea.cards.toList()
            val rivalBoard = rival.playArea.cards.toList()
            val taunts = rivalBoard.filter { it.isTaunt }
            for (attacker in myBoard) {
                if (attacker.isExhausted || attacker.isFrozen || attacker.atc <= 0) continue
                val target = taunts.firstOrNull() ?: rivalBoard.firstOrNull() ?: rival.playArea.hero
                if (target != null) {
                    if (target === rival.playArea.hero) {
                        attacker.action.attackHero()
                    } else {
                        attacker.action.attack(target)
                    }
                    log.info { "兜底攻击: ${attacker.entityName} -> ${target?.entityName}" }
                    Thread.sleep(1000)
                }
            }
            val power = me.playArea.power
            if (power != null && me.usableResource >= power.cost) {
                power.action.power()
                log.info { "兜底使用英雄技能" }
                Thread.sleep(500)
            }
            log.info { "简易兜底完毕" }
        } catch (e: Exception) {
            log.error { "简易兜底异常: ${e.message}" }
        }
    }

}
