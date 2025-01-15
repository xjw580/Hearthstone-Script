package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 奥秘区
 *
 * @author 肖嘉威
 * @date 2022/11/28 20:02
 */
class SecretArea : Area {

    constructor(player: Player) : super(5, player)

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

    fun deepClone(player: Player = this.player, containZeroCards: Boolean = false): SecretArea {
        val area = SecretArea(
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
