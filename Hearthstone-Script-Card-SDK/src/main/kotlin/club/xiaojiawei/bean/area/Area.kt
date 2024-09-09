package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.log
import java.util.*
import kotlin.concurrent.Volatile

/**
 * 区域抽象类
 * @author 肖嘉威
 * @date 2022/11/28 19:48
 */
abstract class Area @JvmOverloads constructor(
    @Volatile var maxSize: Int,
    val defaultMaxSize: Int = maxSize,
    @Volatile var player: Player = Player.UNKNOWN_PLAYER
) {

    val cards: MutableList<Card> = mutableListOf()

    private val zeroCards: MutableMap<String, Card> = mutableMapOf()

    @Volatile
    var oldMaxSize = 0

    constructor(maxSize: Int, player: Player) : this(maxSize, maxSize, player)

    protected fun addZone(card: Card?) {
        card ?: return
        card.area = this
    }

    protected fun removeZone(card: Card?) {
        card ?: return
        card.area = null
    }

    protected open fun addZeroCard(card: Card?) {
        card ?: return
        if (card.entityId.isNotEmpty()){
            zeroCards[card.entityId] = card
            addZone(card)
            if (log.isDebugEnabled()) {
                log.debug { getLogText(card, "zeroArea") }
            }
        }
    }

    protected fun addCard(card: Card?, pos: Int) {
        card ?: return
        if (pos >= cards.size) {
            cards.add(card)
        } else {
            cards.add(pos, card)
        }
        addZone(card)
        log.info { getLogText(card, "") }
    }

    protected fun removeCard(card: Card?): Boolean {
        removeZone(card)
        return cards.remove(card)
    }

    protected fun removeCard(index: Int): Card {
        val remove = cards.removeAt(index)
        removeZone(remove)
        return remove
    }

    protected fun removeZeroCard(entityId: String): Card? {
        if (entityId.isEmpty()) return null
        val remove = zeroCards.remove(entityId)
        removeZone(remove)
        return remove
    }

    protected fun getLogText(card: Card, name: String): String {
        var extraMsg = name
        if (name.isNotEmpty()) {
            extraMsg = String.format("的【%s】", extraMsg)
        }
        val className = this.javaClass.simpleName
        val zoneComment = ZoneEnum.valueOf(className.substring(0, className.lastIndexOf(Area::class.java.simpleName)).uppercase(Locale.getDefault())).comment

        return String.format(
            "向玩家%s【%s】的【%s】%s添加卡牌，entityId:%s，entityName:%s，cardId:%s，size:%d",
            player.playerId,
            player.gameId,
            zoneComment,
            extraMsg,
            card.entityId,
            (if (card.entityName == Entity.UNKNOWN_ENTITY_NAME) "" else card.entityName),
            card.cardId,
            cards.size
        )
    }

    /**
     * 向末尾添加
     * @param card
     * @return
     */
    fun add(card: Card?): Boolean {
        return add(card, cards.size + 1)
    }

    /**
     * @param card
     * @param pos
     * @return
     */
    open fun add(card: Card?, pos: Int): Boolean {
        var position = pos
        var result = true
        if (card == null) {
            result = false
        } else {
            if (position-- <= 0) {
                addZeroCard(card)
            } else {
                addCard(card, position)
            }
        }
        return result
    }

    fun cardSize(): Int {
        return cards.size
    }

    fun indexOfCard(card: Card?): Int {
        card ?: return -2
        return indexOfCard(card.entityId)
    }

    fun indexOfCard(entityId: String): Int {
        if (entityId.isEmpty()) return -2
        if (zeroCards.containsKey(entityId)) return -1

        for (i in cards.indices) {
            if (cards[i].entityId == entityId) {
                return i
            }
        }
        return -2
    }

    open fun findByEntityId(entityId: String): Card? {
        if (entityId.isEmpty()) return null

        var card = zeroCards[entityId]
        if (card == null){
            for (c in cards) {
                if (entityId == c.entityId) {
                    card = c
                    break
                }
            }
        }
        return card
    }

    open fun removeByEntityId(entityId: String): Card? {
        var card = removeByEntityIdInZeroArea(entityId)
        if (card == null){
            for (i in cards.indices) {
                if (entityId == cards[i].entityId) {
                    card = removeCard(i)
                    break
                }
            }
        }
        return card
    }

    fun removeByEntityIdInZeroArea(entityId: String): Card? {
        return removeZeroCard(entityId)
    }

    val isFull: Boolean
        get() = cards.size >= maxSize

    val isEmpty: Boolean
        get() = cards.isEmpty()

    override fun toString(): String {
        return "Area{" +
                "cards=" + cards +
                ", maxSize=" + maxSize +
                '}'
    }
}
