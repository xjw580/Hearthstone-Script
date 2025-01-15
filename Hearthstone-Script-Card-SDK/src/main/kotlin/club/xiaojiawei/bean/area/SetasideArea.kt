package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 除外区（各种效果在此生成）,发现时，最新的三张就是需要选择的牌
 * @author 肖嘉威
 * @date 2022/11/30 14:36
 */
class SetasideArea : Area {

    constructor(player: Player) : super(Int.MAX_VALUE, player)

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

    fun deepClone(player: Player = this.player, containZeroCards: Boolean = false): SetasideArea {
        val area = SetasideArea(
            maxSize,
            defaultMaxSize,
            oldMaxSize,
            player,
            deepCloneCards(),
            if (containZeroCards) deepZeroCards() else zeroCards
        )
        for (card in area.cards) {
            card.area = area
        }
        return area
    }
}
