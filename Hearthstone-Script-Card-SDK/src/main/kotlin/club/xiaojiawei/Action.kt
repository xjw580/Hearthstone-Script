package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import java.util.function.Consumer
import kotlin.math.max

/**
 * @author 肖嘉威
 * @date 2025/1/10 15:28
 */
abstract class Action(
    /**
     * 真正执行
     */
    val exec: Consumer<WarState>,
    /**
     * 模拟执行
     */
    val simulate: Consumer<WarState>
)

open class AttackAction(
    exec: Consumer<WarState>,
    simulate: Consumer<WarState>
) : Action(exec, simulate)

open class PlayAction(
    exec: Consumer<WarState>,
    simulate: Consumer<WarState>
) : Action(exec, simulate)

private val empty: Consumer<WarState> = Consumer {}

object TurnOverAction : Action(empty, empty)

object InitAction : Action(empty, empty)

class WarState(var me: Player, var rival: Player) : Cloneable {
    public override fun clone(): WarState {
        return WarState(me.clone(), rival.clone())
    }

    fun isEnd(): Boolean {
        rival.playArea.hero?.let { rivalHero ->
            if (rivalHero.blood() <= 0) return true
            me.playArea.hero?.let { myHero ->
                return myHero.blood() <= 0
            }
        }
        return true
    }

    fun calcScore(): Double {
        return calcPlayerScore(me) - calcPlayerScore(rival)
    }

    fun calcPlayerScore(player: Player): Double {
        var score = 0.0
        player.playArea.cards.forEach { card ->
            score += calcCardScore(card)
        }
        player.playArea.hero?.let { hero ->
            score += calcCardScore(hero)
        }
        player.playArea.weapon?.let { weapon ->
            score += calcCardScore(weapon) * 0.8
        }
        return score
    }

    fun calcCardScore(card: Card): Double {
        if (card.isSurvival()) {
            val basicRatio = CARD_WEIGHT_TRIE[card.cardId]?.weight ?: 1.0
            val atc = max(card.atc, 0).toDouble()
            val blood = max(card.blood(), 0).toDouble()
            val basicScore = atc * 1.5 + blood
            var score: Double = basicScore + if (card.cardType === CardTypeEnum.HERO) Int.MAX_VALUE.toDouble() else 0.0
            if (card.isDeathRattle) {
                score -= 0.3
            }
            if (card.isTaunt) {
                score += 1
            }
            if (card.isAdjacentBuff) {
                score += 2
            }
            if (card.isAura) {
                score += 2
            }
            if (card.isWindFury) {
                score += 0.5 * atc
            }
            if (card.isMegaWindfury) {
                score += 0.9 * atc
            }
            if (card.isTitan) {
                score += 8
            }
            if (card.isTriggerVisual) {
                score += 0.8
            }
            score += card.spellPower * 1
            return score * basicRatio
        }
        return 0.0
    }
}

