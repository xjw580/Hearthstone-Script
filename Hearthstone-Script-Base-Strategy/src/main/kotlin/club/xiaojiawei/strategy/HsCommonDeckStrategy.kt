package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsCommonDeckStrategy : DeckStrategy() {
    override fun name(): String = "基础策略"

    override fun description(): String = "未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-0-base-deck-97e9-1f4e126cd33b"

    override fun referWeight(): Boolean = true

    override fun referPowerWeight(): Boolean = true

    override fun executeChangeCard(cards: HashSet<Card>) {
        cards.removeIf { card -> card.cost > 2 }
    }

    override fun executeOutCard() {
        val me = WAR.me
        val rival = WAR.rival

        DeckStrategyUtil.powerCard(me, rival)

        DeckStrategyUtil.cleanPlay()

        DeckStrategyUtil.powerCard(me, rival)

//        使用技能
        me.playArea.power?.let {
            if (me.usableResource >= it.cost || it.cost == 0) {
                it.action.power()
            }
        }
        DeckStrategyUtil.cleanPlay()

        me.playArea.cards.toTypedArray().forEach { card: Card ->
            if (card.isLaunchpad && me.usableResource >= card.launchCost()) {
                card.action.launch()
            }
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = 1
}
