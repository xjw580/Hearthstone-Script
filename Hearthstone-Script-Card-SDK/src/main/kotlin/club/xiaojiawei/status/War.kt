package club.xiaojiawei.status

import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.safeRun
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.mapper.WarMapper
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

class War(var allowLog: Boolean = false) : Cloneable {

    @Volatile
    var currentPlayer: Player = Player.INVALID_PLAYER
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
    var me: Player = Player.INVALID_PLAYER

    @Volatile
    var rival: Player = Player.INVALID_PLAYER

    @Volatile
    var player1: Player = Player("1", allowLog = true)

    @Volatile
    var player2: Player = Player("2", allowLog = true)

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

    fun exchangePlayer() {
        val tempMe = me
        me = rival
        rival = tempMe
        val tempPlayer1 = player1
        player1 = player2
        player2 = tempPlayer1
    }

    public override fun clone(): War {
        val newWar = WarMapper.INSTANCE.clone(this)
        val oldMe = newWar.me
        newWar.me = newWar.me.clone()
        newWar.rival = newWar.rival.clone()
        if (oldMe == newWar.player1) {
            newWar.player1 = newWar.me
            newWar.player2 = newWar.rival
        } else {
            newWar.player1 = newWar.rival
            newWar.player2 = newWar.me
        }
        return newWar
    }
}