package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.RunModeEnum
import java.util.HashSet

/**
 * @author 肖嘉威
 * @date 2024/10/14 16:31
 */
class HsSurrenderDeckStrategy:DeckStrategy() {
    override fun name(): String {
        return "秒投策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.WILD, RunModeEnum.STANDARD, RunModeEnum.CASUAL)
    }

    override fun deckCode(): String {
        return ""
    }

    override fun id(): String {
        return "e71234fa-base-deck-97e9-surrender"
    }

    override fun reset() {
        super.reset()
        needSurrender = true
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
    }

    override fun executeOutCard() {
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 0
    }
}