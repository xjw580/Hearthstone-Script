package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MutableCardList
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area.Companion.UNKNOWN_AREA
import club.xiaojiawei.config.ENABLE_AREA_LOG
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.util.isTrue
import java.util.*

/**
 * 区域抽象类
 * @author 肖嘉威
 * @date 2022/11/28 19:48
 */

abstract class Area(
    /**
     * 允许打印日志
     */
    val allowLog: Boolean = false,
    maxSize: Int,
    val defaultMaxSize: Int = maxSize,
    @Volatile var oldMaxSize: Int = 0,
    player: Player? = null,
) {

    val cards: MutableCardList = MutableCardList()

    /**
     * 零区，即等待区，日志中下标为0表示进入零区
     */
    private val zeroCards: MutableMap<String, Card> = mutableMapOf()

    val player: Player by lazy { player ?: Player.UNKNOWN_PLAYER }

    init {
        for (card in cards) {
            addZone(card)
        }
    }

    protected fun zeroCardSize(): Int {
        return zeroCards.size
    }

    @Volatile
    var maxSize = maxSize
        set(value) {
            val zoneComment = getChineseName()
            (allowLog && ENABLE_AREA_LOG).isTrue {
                log.info { "玩家${player.playerId}【${player.gameId}】的【${zoneComment}】的【maxSize】更改为【${value}】" }
            }
            field = value
        }


    constructor(maxSize: Int, player: Player) : this(maxSize = maxSize, defaultMaxSize = maxSize, player = player)

    protected fun addZone(card: Card?) {
        card ?: return
        card.area = this
    }

    protected fun removeZone(card: Card?) {
        card ?: return
        card.area = UNKNOWN_AREA
    }

    protected open fun addZeroCard(card: Card?) {
        card ?: return
        if (card.entityId.isNotEmpty()) {
            zeroCards[card.entityId] = card
            addZone(card)
            if (log.isDebugEnabled() && allowLog && ENABLE_AREA_LOG) {
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
        (allowLog && ENABLE_AREA_LOG).isTrue {
            log.info { getLogText(card, "") }
        }
    }

    protected fun removeCard(card: Card?): Boolean {
        removeZone(card)
        return cards.remove(card)
    }

    open protected fun removeCard(index: Int): Card? {
        if (index >= cards.size || index < 0) return null
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
        val zoneComment = getChineseName()

        return String.format(
            "向玩家%s%s的【%s】%s添加卡牌，entityId:%s，entityName:%s，cardId:%s，size:%d",
            player.playerId,
            if (player.gameId.isBlank()) "" else "【${player.gameId}】",
            zoneComment,
            extraMsg,
            card.entityId,
            card.getFormatEntityName(),
            card.cardId,
            cards.size
        )
    }

    private fun getChineseName(): String {
        val className = this.javaClass.simpleName
        return ZoneEnum.valueOf(
            className.substring(0, className.lastIndexOf(Area::class.java.simpleName)).uppercase(Locale.getDefault())
        ).comment

    }

    /**
     * 向末尾添加卡牌
     * 不会进行full检查
     * @param card
     * @return
     */
    fun add(card: Card?): Boolean {
        return add(card, cards.size + 1)
    }

    /**
     * 向末尾添加卡牌
     * 会进行full检查
     * @param card
     * @return
     */
    fun safeAdd(card: Card?): Boolean {
        if (isFull) return false
        return add(card)
    }

    /**
     * 向指定位置添加卡牌
     * 不会进行full检查
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

    open fun cardSize(): Int {
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

    fun remove(cardIndex: Int): Card? {
        return removeCard(cardIndex)
    }

    open fun findByEntityId(entityId: String): Card? {
        if (entityId.isEmpty()) return null

        var card = zeroCards[entityId]
        if (card == null) {
            for (c in cards) {
                if (entityId == c.entityId) {
                    card = c
                    break
                }
            }
        }
        return card
    }

    open fun findBygCardId(cardId: String): Card? {
        if (cardId.isEmpty()) return null

        var card: Card? = null
        for (entry in zeroCards) {
            if (entry.value.cardId == cardId) {
                card = entry.value
                break
            }
        }
        if (card == null) {
            for (c in cards) {
                if (cardId == c.cardId) {
                    card = c
                    break
                }
            }
        }
        return card
    }

    open fun removeByEntityId(entityId: String): Card? {
        var card = removeByEntityIdInZeroArea(entityId)
        if (card == null) {
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

    companion object {
        val UNKNOWN_AREA: Area = object : Area(allowLog = false, maxSize = 0) {}
    }
}

fun Area.safeRun(block: (Area) -> Unit): Area {
    if (isInValid()) {
        return this
    } else {
        block(this)
        return this
    }
}

fun Area.isValid(): Boolean {
    return this !== UNKNOWN_AREA
}

fun Area.isInValid(): Boolean {
    return this === UNKNOWN_AREA
}