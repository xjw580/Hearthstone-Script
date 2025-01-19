package club.xiaojiawei.status

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.bean.safeRun
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.mapper.WarMapper
import club.xiaojiawei.status.War.Companion.UNKNOWN_WAR
import club.xiaojiawei.util.isTrue

/**
 * 游戏对局状态
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 */

/**
 * 全局war
 */
val WAR: War = War(true)

class War(
    /**
     * 允许打印日志
     */
    val allowLog: Boolean = false
) : Cloneable {

    /**
     * 存储当前war中所有的[Card]
     * k:[club.xiaojiawei.bean.Entity.entityId]
     */
    val cardMap: MutableMap<String?, Card?> = HashMap()

    @Volatile
    var currentPlayer: Player = Player.UNKNOWN_PLAYER
        @Synchronized set(value) {
            field = value
            value.safeRun {
                allowLog.isTrue {
                    log.info { "${value.gameId} 的回合" }
                }
            }
        }

    @Volatile
    var firstPlayerGameId: String = ""
        @Synchronized set(value) {
            field = value
            (value.isNotEmpty() && allowLog).isTrue {
                log.info { "先手玩家：$value" }
            }
        }

    @Volatile
    var currentPhase = WarPhaseEnum.FILL_DECK

    @Volatile
    var currentTurnStep: StepEnum? = null
        @Synchronized set(value) {
            field = value
            field?.let {
                allowLog.isTrue {
                    log.info { it.comment }
                }
            }
        }

    @Volatile
    var me: Player = Player.UNKNOWN_PLAYER

    @Volatile
    var rival: Player = Player.UNKNOWN_PLAYER

    @Volatile
    var player1: Player = Player.UNKNOWN_PLAYER

    @Volatile
    var player2: Player = Player.UNKNOWN_PLAYER

    /**
     * 总回合数
     */
    @Volatile
    var warTurn = 0

    /**
     * 胜者
     */
    @Volatile
    var won: String = ""

    /**
     * 败者
     */
    @Volatile
    var lost: String = ""

    /**
     * 投降者
     */
    @Volatile
    var conceded: String = ""

    /**
     * 本局开始时间
     */
    @Volatile
    var startTime: Long = 0

    /**
     * 本局结束时间
     */
    @Volatile
    var endTime: Long = 0

    @Volatile
    var isMyTurn = false

    @Volatile
    var currentRunMode: RunModeEnum? = null

    /**
     * 所生成最大的entityId，entityId越大表明entity生成的时间越晚
     */
    @Volatile
    var maxEntityId: String? = null

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

