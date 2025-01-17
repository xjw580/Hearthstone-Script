package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 牌库区
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class DeckArea : Area {

    constructor(player: Player) : super(60, player)

    private constructor(
        maxSize: Int,
        defaultMaxSize: Int,
        oldMaxSize: Int,
        player: Player,
        cards: MutableList<Card>,
        zeroCards: MutableMap<String, Card>,
    ) : super(maxSize, defaultMaxSize, oldMaxSize, player, cards, zeroCards, false)

    override fun addZeroCard(card: Card?) {
        add(card)
    }

    fun deepClone(player: Player = this.player, containZeroCards: Boolean = false): DeckArea {
        val area = DeckArea(
            maxSize,
            defaultMaxSize,
            oldMaxSize,
            player,
            deepCloneCards(),
            if (containZeroCards) deepZeroCards() else zeroCards
        )
        return area
    }

}
