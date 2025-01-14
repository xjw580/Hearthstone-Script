package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.util.isTrue

/**
 * 战场
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class PlayArea : Area {

    constructor(player: Player) : super(7, player)

    private constructor(
        maxSize: Int,
        defaultMaxSize: Int,
        oldMaxSize: Int,
        player: Player,
        cards: MutableList<Card>,
        zeroCards: MutableMap<String, Card>,
    ) : super(maxSize, defaultMaxSize, oldMaxSize, player, cards, zeroCards, false)

    @Volatile
    var hero: Card? = null

    @Volatile
    var heroHide: Card? = null

    @Volatile
    var power: Card? = null

    @Volatile
    var powerHide: Card? = null

    @Volatile
    var weapon: Card? = null

    @Volatile
    var weaponHide: Card? = null

    private fun addZoneAndLog(name: String, card: Card) {
        addZone(card)

        allowLog.isTrue {
            log.info { getLogText(card, name) }
        }
    }

    override fun add(card: Card?, pos: Int): Boolean {
        var result = true
        if (card == null) {
            result = false
        } else if (card.cardType === CardTypeEnum.HERO_POWER) {
            powerHide = power
            power = card
            addZoneAndLog("技能", card)
        } else if (card.cardType === CardTypeEnum.HERO) {
            heroHide = hero
            hero = card
            addZoneAndLog("英雄", card)
        } else if (card.cardType === CardTypeEnum.WEAPON) {
            weaponHide = weapon
            weapon = card
            addZoneAndLog("武器", card)
        } else {
            result = super.add(card, pos)
        }
        return result
    }

    override fun findByEntityId(entityId: String): Card? {
        var card = super.findByEntityId(entityId)
        if (card == null) {
            if (hero != null && hero!!.entityId == entityId) {
                card = hero
            } else if (power != null && power!!.entityId == entityId) {
                card = power
            } else if (weapon != null && weapon!!.entityId == entityId) {
                card = weapon
            } else if (heroHide != null && heroHide!!.entityId == entityId) {
                card = heroHide
            } else if (powerHide != null && powerHide!!.entityId == entityId) {
                card = powerHide
            } else if (weaponHide != null && weaponHide!!.entityId == entityId) {
                card = weaponHide
            }
        }
        return card
    }


    override fun removeByEntityId(entityId: String): Card? {
        var card = super.removeByEntityId(entityId)
        if (card == null) {
            if (hero != null && hero!!.entityId == entityId) {
                card = hero
                hero = null
            } else if (power != null && power!!.entityId == entityId) {
                card = power
                power = null
            } else if (weapon != null && weapon!!.entityId == entityId) {
                card = weapon
                weapon = null
            } else if (heroHide != null && heroHide!!.entityId == entityId) {
                card = heroHide
                heroHide = null
            } else if (powerHide != null && powerHide!!.entityId == entityId) {
                card = powerHide
                powerHide = null
            } else if (weaponHide != null && weaponHide!!.entityId == entityId) {
                card = weaponHide
                weaponHide = null
            }
        }
        return card
    }

    fun deepClone(player: Player = this.player, containZeroCards: Boolean = false): PlayArea {
        val playArea = PlayArea(
            maxSize,
            defaultMaxSize,
            oldMaxSize,
            player,
            deepCloneCards(),
            if (containZeroCards) deepZeroCards() else zeroCards
        )
        playArea.hero = hero?.clone()
        playArea.weapon = weapon?.clone()
        playArea.power = power?.clone()
        return playArea
    }
}
