package club.xiaojiawei.bean

import club.xiaojiawei.bean.War.Companion.UNKNOWN_WAR
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.config.ENABLE_WAR_LOG
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.mapper.WarMapper
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2025/1/22 16:53
 */
class War(
    /**
     * 允许打印日志
     */
    val allowLog: Boolean = false
) : BaseWar(), Cloneable {

    /**
     * 选择卡牌时间
     */
    @Volatile
    var isChooseCardTime = false

    /**
     * 存储当前war中所有的[Card]
     * k:[club.xiaojiawei.bean.Entity.entityId]
     */
    val cardMap: MutableMap<String, Card> = HashMap()

    override var currentPlayer: Player
        get():Player {
            return super.currentPlayer
        }
        @Synchronized set(value) {
            super.currentPlayer = value
            value.safeRun {
                (allowLog && ENABLE_WAR_LOG).isTrue {
                    log.info { "${value.gameId} 的回合" }
                }
            }
        }

    override var firstPlayerGameId: String
        get():String {
            return super.firstPlayerGameId
        }
        @Synchronized set(value) {
            super.firstPlayerGameId = value
            (value.isNotEmpty() && allowLog && ENABLE_WAR_LOG).isTrue {
                log.info { "先手玩家：$value" }
            }
        }

    override var currentTurnStep: StepEnum?
        get():StepEnum? {
            return super.currentTurnStep
        }
        @Synchronized set(value) {
            super.currentTurnStep = value?.also {
                (allowLog && ENABLE_WAR_LOG).isTrue {
                    log.info { it.comment }
                }
            }
        }

    /**
     * 最大entityId自增1
     */
    fun incrementMaxEntityId(): String {
        val nextMaxEntityId = ((maxEntityId?.toInt() ?: 0) + 1).toString()
        this.maxEntityId = nextMaxEntityId
        return nextMaxEntityId
    }

    fun exchangePlayer() {
        val tempMe = me
        me = rival
        rival = tempMe
        val tempPlayer1 = player1
        player1 = player2
        player2 = tempPlayer1
    }

    /**
     * 添加卡牌，当新生成的[Card]需要添加至[Area]时推荐用此方法代替[Area.add]，这会缓存此[Card]
     */
    fun addCard(card: Card, area: Area, pos: Int? = null) {
        cardMap[card.entityId] = card
        if (pos == null) {
            area.add(card)
        } else {
            area.add(card, pos)
        }
    }

    /**
     * 判断战局是否结束
     */
    fun isEnd(): Boolean {
        val rivalHero = rival.playArea.hero
        val myHero = me.playArea.hero
        if (rivalHero == null || myHero == null) return true
        return !myHero.isAlive() || !rivalHero.isAlive()
    }

    public override fun clone(): War {
        return deepClone(false)
    }

    fun deepClone(allowLog: Boolean): War {
        val newWar = War(allowLog = allowLog)
        WarMapper.INSTANCE.update(this, newWar)
        val newMe = this.me.deepClone(newWar)
        val newRival = this.rival.deepClone(newWar)
        newWar.me = newMe
        newWar.rival = newRival
        if (this.me == this.player1) {
            newWar.player1 = newMe
            newWar.player2 = newRival
        } else {
            newWar.player1 = newRival
            newWar.player2 = newMe
        }
        if (this.me == this.currentPlayer) {
            newWar.currentPlayer = newMe
        } else {
            newWar.currentPlayer = newRival
        }
        return newWar
    }

    companion object {
        val UNKNOWN_WAR: War = War(false)
    }
}

fun War.safeRun(block: (War) -> Unit): War {
    if (isInValid()) {
        return this
    } else {
        block(this)
        return this
    }
}

fun War.isValid(): Boolean {
    return this !== UNKNOWN_WAR
}

fun War.isInValid(): Boolean {
    return this === UNKNOWN_WAR
}