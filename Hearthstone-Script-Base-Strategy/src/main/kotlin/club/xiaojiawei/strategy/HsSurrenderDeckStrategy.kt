package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.RunModeEnum

/**
 * @author 肖嘉威
 * @date 2024/10/14 16:31
 */
class HsSurrenderDeckStrategy : DeckStrategy() {
    override fun name(): String = "秒投策略"

    override fun description(): String = "开局秒投"

    override fun getRunMode(): Array<RunModeEnum> = arrayOf(RunModeEnum.WILD, RunModeEnum.STANDARD, RunModeEnum.CASUAL)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-2-base-deck-97e9-surrender"

    override fun reset() {
        super.reset()
        needSurrender = true
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
    }

    override fun executeOutCard() {
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = 0
}
