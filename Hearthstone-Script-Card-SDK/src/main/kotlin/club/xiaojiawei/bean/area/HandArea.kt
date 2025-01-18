package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import kotlin.random.Random

/**
 * 手牌区
 *
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class HandArea : Area {
    constructor(player: Player) : super(10, player)

    private constructor(
        maxSize: Int,
        defaultMaxSize: Int,
        oldMaxSize: Int,
        player: Player,
        cards: MutableList<Card>,
        zeroCards: MutableMap<String, Card>,
    ) : super(maxSize, defaultMaxSize, oldMaxSize, player, cards, zeroCards, false)

    fun deepClone(player: Player = this.player, containZeroCards: Boolean = false): HandArea {
        val area = HandArea(
            maxSize,
            defaultMaxSize,
            oldMaxSize,
            player,
            deepCloneCards(),
            if (containZeroCards) deepZeroCards() else zeroCards
        )
        return area
    }

    fun drawCard(): Card? {
        val deckArea = player.deckArea
        if (deckArea.isEmpty) {
            player.playArea.hero?.let { hero ->
                hero.damage += player.incrementFatigue()
            }
        } else {
            deckArea.remove(deckArea.cardSize() - 1)?.let { removeCard ->
                removeCard.apply {
//                     todo 应该从实际套牌中随机获取
                    cardType = CardTypeEnum.SPELL
                    cost = Random.nextInt(11)
                }
                if (player.handArea.safeAdd(removeCard)) {
                    return removeCard
                }
            }
        }
        return null
    }

}
