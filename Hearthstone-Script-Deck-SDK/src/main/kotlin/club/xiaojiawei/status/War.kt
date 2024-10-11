package club.xiaojiawei.status

import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.safeRun
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.util.isTrue

/**
 * 游戏对局状态
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 */
object War {

    @Volatile
    var currentPlayer: Player = Player.INVALID_PLAYER
        @Synchronized set(value) {
            field = value
            value.safeRun {
                log.info { "${value.gameId} 的回合" }
            }
        }

    @Volatile
    var firstPlayerGameId: String = ""
        @Synchronized set(value) {
            field = value
            value.isNotEmpty().isTrue {
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
                log.info { it.comment }
            }
        }

    @Volatile
    var me: Player = Player.INVALID_PLAYER

    @Volatile
    var rival: Player = Player.INVALID_PLAYER

    @Volatile
    var player1: Player = Player("1")

    @Volatile
    var player2: Player = Player("2")

    @Volatile
    var warTurn = 0

    @Volatile
    var won: String = ""

    @Volatile
    var lost: String = ""

    @Volatile
    var conceded: String = ""

    @Volatile
    var startTime: Long = 0

    @Volatile
    var endTime: Long = 0

    @Volatile
    var isMyTurn = false

    @Volatile
    var currentRunMode: RunModeEnum? = null

}
