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
class PlayArea(allowLog: Boolean = false, player: Player) : Area(allowLog = allowLog, maxSize = 7, player = player) {

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

    /**
     * 获取当前法强
     */
    fun getSpellPower(): Int {
        return cards.sumOf { card -> card.spellPower } +
                (hero?.spellPower ?: 0) + (power?.spellPower ?: 0) + (weapon?.spellPower ?: 0)
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
            if (hero?.entityId == entityId) {
                card = hero
            } else if (power?.entityId == entityId) {
                card = power
            } else if (weapon?.entityId == entityId) {
                card = weapon
            } else if (heroHide?.entityId == entityId) {
                card = heroHide
            } else if (powerHide?.entityId == entityId) {
                card = powerHide
            } else if (weaponHide?.entityId == entityId) {
                card = weaponHide
            }
        }
        return card
    }


    override fun removeByEntityId(entityId: String): Card? {
        var card = super.removeByEntityId(entityId)
        if (card == null) {
            if (hero?.entityId == entityId) {
                card = hero
                hero = null
            } else if (power?.entityId == entityId) {
                card = power
                power = null
            } else if (weapon?.entityId == entityId) {
                card = weapon
                weapon = null
            } else if (heroHide?.entityId == entityId) {
                card = heroHide
                heroHide = null
            } else if (powerHide?.entityId == entityId) {
                card = powerHide
                powerHide = null
            } else if (weaponHide?.entityId == entityId) {
                card = weaponHide
                weaponHide = null
            }
        }
        return card
    }

    override fun removeCard(index: Int): Card? {
        val removeCard = super.removeCard(index)
        removeCard?.let {
            if (it.isStarshipPiece && it.isDead()) {
                player.starshipAtc += it.atc
                player.starshipHealth += it.health
            }
        }
        return removeCard
    }


}
