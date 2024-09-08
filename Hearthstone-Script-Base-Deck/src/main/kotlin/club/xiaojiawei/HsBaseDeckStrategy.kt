package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.War

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsBaseDeckStrategy: DeckStrategy() {
    override fun name(): String {
        return "基础策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD)
    }

    override fun deckCode(): String {
        return ""
    }

    override fun id(): String {
        return "e71234fa-base-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        cards.removeIf { card -> card.cost > 2 }
    }

    override fun executeOutCard() {
        val me = War.me
        val rival = War.rival
        me?:return
        rival?:return
        
        val handArea = me.handArea
        val playArea = me.playArea
        val rivalPlayArea = rival.playArea

        val cards: MutableList<Card> = ArrayList(handArea.cards)
        for (card in cards) {
            if (card.cardType == CardTypeEnum.MINION && !card.isBattlecry && me.usableResource >= card.cost) {
                card.action.power()
            }
        }

        val rivalPlayCards = ArrayList(rivalPlayArea.cards)
        cards.clear()
        cards.addAll(playArea.cards)
        for (rivalPlayCard in rivalPlayCards) {
            if (rivalPlayCard.isTaunt && rivalPlayCard.health - rivalPlayCard.damage > 0) {
                for (card in cards) {
                    if (!card.isExhausted) {
                        card.action.attackMinion(rivalPlayCard)
                    }
                }
            }
        }

        for (card in cards) {
            if (!card.isExhausted) {
                card.action.attackHero()
            }
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 0
    }
}