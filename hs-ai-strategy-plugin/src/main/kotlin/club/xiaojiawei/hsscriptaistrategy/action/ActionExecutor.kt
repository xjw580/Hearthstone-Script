package club.xiaojiawei.hsscriptaistrategy.action

import club.xiaojiawei.hsscriptaistrategy.prompt.LlmAction
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum

object ActionExecutor {

    fun execute(action: LlmAction, me: Player, rival: Player): Boolean =
        try {
            when (action.action) {
                "play_card" -> executePlayCard(action, me, rival)
                "attack" -> executeAttack(action, me, rival)
                "hero_power" -> executeHeroPower(action, me, rival)
                "launch" -> executeLaunch(action, me)
                "forge" -> executeForge(action, me)
                "trade" -> executeTrade(action, me)
                "end_turn" -> true
                else -> {
                    log.warn { "未知动作类型: ${action.action}" }
                    false
                }
            }
        } catch (e: Exception) {
            log.error { "执行动作异常: ${action.action}, ${e.message}" }
            false
        }

    private fun executePlayCard(action: LlmAction, me: Player, rival: Player): Boolean {
        val idx = action.cardIndex
        if (idx == null || idx < 0) {
            log.warn { "play_card 缺少 card_index" }
            return false
        }
        val hand = me.handArea.cards
        if (idx >= hand.size) {
            log.warn { "play_card 手牌下标越界: $idx (共${hand.size}张)" }
            return false
        }
        val card = hand[idx]
        val targetSide = action.targetSide
        val targetIndex = action.targetIndex
        if (targetSide != null && targetIndex != null) {
            val target = resolveTarget(targetSide, targetIndex, me, rival)
            if (target == null) {
                log.warn { "play_card 目标为空: side=$targetSide, index=$targetIndex" }
                return false
            }
            val result = card.action.power(target)
            if (result == null) {
                log.warn { "play_card 指向目标后仍失败: ${card.entityName}(${card.cardId}), 可能法力不足或目标无效" }
            }
            return result != null
        }
        val result = card.action.power()
        if (result == null) {
            val hint = if (card.isBattlecry || card.cardType == CardTypeEnum.SPELL) {
                "该卡牌可能需要指定目标(target_index+target_side)，或法力不足"
            } else {
                "可能法力不足或卡牌限制"
            }
            log.warn { "play_card 失败: ${card.entityName}(${card.cardId}), cost=${card.cost}, $hint" }
        }
        if (result != null && card.isChooseOne && action.chooseOneIndex != null) {
            card.action.chooseOne(action.chooseOneIndex)
            log.info { "play_card 抉择选择: ${card.entityName}, option=${action.chooseOneIndex}" }
        }
        return result != null
    }

    private fun executeAttack(action: LlmAction, me: Player, rival: Player): Boolean {
        val attackerIdx = action.attackerIndex
        val targetIdx = action.targetIndex
        if (attackerIdx == null || targetIdx == null) {
            log.warn { "attack 缺少 attacker_index 或 target_index" }
            return false
        }
        val attacker: Card? = if (attackerIdx == -1) {
            me.playArea.hero
        } else {
            me.playArea.cards.getOrNull(attackerIdx)
        }
        if (attacker == null) {
            log.warn { "attack 攻击者为空, attacker_index=$attackerIdx" }
            return false
        }
        if (targetIdx == -1) {
            return attacker.action.attackHero() != null
        }
        val target = rival.playArea.cards.getOrNull(targetIdx)
        if (target == null) {
            log.warn { "attack 目标为空, target_index=$targetIdx" }
            return false
        }
        return attacker.action.attack(target) != null
    }

    private fun executeHeroPower(action: LlmAction, me: Player, rival: Player): Boolean {
        val power = me.playArea.power
        if (power == null) {
            log.warn { "hero_power 英雄技能为空" }
            return false
        }
        val targetSide = action.targetSide
        val targetIndex = action.targetIndex
        if (targetSide != null && targetIndex != null) {
            val target = resolveTarget(targetSide, targetIndex, me, rival)
            if (target == null) {
                log.warn { "hero_power 目标为空" }
                return false
            }
            return power.action.power(target) != null
        }
        val result = power.action.power()
        if (result == null) {
            log.warn { "hero_power 失败: ${power.entityName}, 可能法力不足或需要指定目标" }
        }
        return result != null
    }

    private fun executeLaunch(action: LlmAction, me: Player): Boolean {
        val idx = action.cardIndex
        if (idx == null || idx < 0) {
            log.warn { "launch 缺少 card_index" }
            return false
        }
        val card = me.playArea.cards.getOrNull(idx)
        if (card == null) {
            log.warn { "launch 场上无此下标: $idx" }
            return false
        }
        return card.action.launch() != null
    }

    private fun executeForge(action: LlmAction, me: Player): Boolean {
        val idx = action.cardIndex
        if (idx == null || idx < 0) {
            log.warn { "forge 缺少 card_index" }
            return false
        }
        val hand = me.handArea.cards
        if (idx >= hand.size) {
            log.warn { "forge 手牌下标越界: $idx" }
            return false
        }
        val card = hand[idx]
        if (!card.isForge) {
            log.warn { "forge 该卡牌不可锻造: ${card.entityName}(${card.cardId})" }
            return false
        }
        val result = card.action.forge()
        if (result == null) {
            log.warn { "forge 失败: ${card.entityName}(${card.cardId}), 可能法力不足" }
        }
        return result != null
    }

    private fun executeTrade(action: LlmAction, me: Player): Boolean {
        val idx = action.cardIndex
        if (idx == null || idx < 0) {
            log.warn { "trade 缺少 card_index" }
            return false
        }
        val hand = me.handArea.cards
        if (idx >= hand.size) {
            log.warn { "trade 手牌下标越界: $idx" }
            return false
        }
        val card = hand[idx]
        if (!card.isTradeable) {
            log.warn { "trade 该卡牌不可交易: ${card.entityName}(${card.cardId})" }
            return false
        }
        val result = card.action.trade()
        if (result == null) {
            log.warn { "trade 失败: ${card.entityName}(${card.cardId}), 可能法力不足(需1费)" }
        }
        return result != null
    }

    private fun resolveTarget(side: String, index: Int, me: Player, rival: Player): Card? {
        val player = if (side == "me") me else rival
        return if (index == -1) {
            player.playArea.hero
        } else {
            player.playArea.cards.getOrNull(index)
        }
    }

}
