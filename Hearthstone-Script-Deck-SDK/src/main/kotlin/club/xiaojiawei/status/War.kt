package club.xiaojiawei.status

import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.StepEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.log
import javafx.beans.property.SimpleIntegerProperty
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import kotlin.concurrent.Volatile
import kotlin.math.min

/**
 * 游戏对局状态
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 */
object War {

    @Volatile
    var currentPlayer: Player? = null
        @Synchronized set(value) {
            field = value
            field?.let {
                log.info { "${it.gameId} 的回合" }
            }
        }

    @Volatile
    var firstPlayerGameId: String = ""
        @Synchronized set(value) {
            field = value
            log.info { "先手玩家：$value" }
        }

    @Volatile
    var currentPhase = WarPhaseEnum.FILL_DECK_PHASE

    @Volatile
    var currentTurnStep: StepEnum? = null
        @Synchronized set(value) {
            field = value
            field?.let {
                log.info { it.comment }
            }
        }

    @Volatile
    var me: Player? = null

    @Volatile
    var rival: Player? = null

    @Volatile
    var player1: Player? = null

    @Volatile
    var player2: Player? = null

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

    private val resetCallbackList: MutableList<Runnable> = ArrayList()

    private val endCallbackList: MutableList<Runnable> = ArrayList()

    @JvmField
    val WAR_COUNT: SimpleIntegerProperty = SimpleIntegerProperty()

    @JvmField
    val WIN_COUNT: AtomicInteger = AtomicInteger()

    /**
     * 单位：min
     */
    @JvmField
    val GAME_TIME: AtomicInteger = AtomicInteger()

    @JvmField
    val EXP: AtomicInteger = AtomicInteger()

    @Synchronized
    fun reset() {
        firstPlayerGameId = ""
        currentPhase = WarPhaseEnum.FILL_DECK_PHASE
        currentTurnStep = null
        rival = null
        me = rival
        currentPlayer = me
        player1 = Player("1")
        player2 = Player("2")
        warTurn = 0
        conceded = ""
        lost = conceded
        won = lost
        endTime = 0
        startTime = endTime
        resetCallbackList.forEach(Consumer { obj: Runnable -> obj.run() })
        log.info { "已重置游戏状态" }
    }

    @Synchronized
    fun addResetCallback(runnable: Runnable) {
        resetCallbackList.add(runnable)
    }

    @Synchronized
    fun addEndCallback(runnable: Runnable) {
        endCallbackList.add(runnable)
    }

    @Synchronized
    fun startWar(runModeEnum: RunModeEnum?) {
        currentRunMode = runModeEnum
    }

    @Synchronized
    fun endWar() {
        var flag = false
        if (me != null) {
            flag = printResult()
        }
        val time = (endTime - startTime) / 1000 / 60
        log.info("本局游戏时长：" + time + "分钟")
        GAME_TIME.set((time + GAME_TIME.get()).toInt())
        val winExp: Int
        val lostExp: Int
        when (currentRunMode) {
            RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.CLASSIC, RunModeEnum.TWIST -> {
                winExp = 8
                lostExp = 6
            }

            RunModeEnum.CASUAL, RunModeEnum.BACON -> {
                winExp = 6
                lostExp = 4
            }

            else -> {
                winExp = 0
                lostExp = 0
                log.warn("未知模式，增加经验值0")
            }
        }
        val earnExp = (min(time.toDouble(), 30.0) * (if (flag) winExp else lostExp)).toLong()
        log.info("本局游戏获得经验值：$earnExp")
        EXP.set((EXP.get() + earnExp).toInt())
        endCallbackList.forEach(Consumer { obj: Runnable -> obj.run() })
        WAR_COUNT.set(WAR_COUNT.get() + 1)
    }

    private fun printResult(): Boolean {
        var flag = false
        if (won == me?.gameId) {
            WIN_COUNT.incrementAndGet()
            flag = true
        }
        log.info("本局游戏胜者：" + won)
        log.info("本局游戏败者：" + lost)
        log.info("本局游戏投降者：" + conceded)
        return flag
    }

    @Synchronized
    fun getPlayer(playerId: String): Player? {
        return if (player1!!.playerId == playerId) player1 else player2
    }

    @Synchronized
    fun getReversePlayer(playerId: String): Player? {
        return if (player1!!.playerId == playerId) player2 else player1
    }

    @Synchronized
    fun getPlayerByArea(area: Area): Player? {
        if (area === player1!!.playArea || area === player1!!.handArea || area === player1!!.deckArea || area === player1!!.graveyardArea || area === player1!!.removedfromgameArea || area === player1!!.secretArea || area === player1!!.setasideArea
        ) {
            return player1
        }
        return player2
    }

    @Synchronized
    fun getReverseArea(area: Area): Area? {
        if (area === player1!!.playArea) {
            return player2!!.playArea
        } else if (area === player1!!.handArea) {
            return player2!!.handArea
        } else if (area === player1!!.deckArea) {
            return player2!!.deckArea
        } else if (area === player1!!.graveyardArea) {
            return player2!!.graveyardArea
        } else if (area === player1!!.removedfromgameArea) {
            return player2!!.removedfromgameArea
        } else if (area === player1!!.secretArea) {
            return player2!!.secretArea
        } else if (area === player1!!.setasideArea) {
            return player2!!.setasideArea
        } else if (area === player2!!.playArea) {
            return player1!!.playArea
        } else if (area === player2!!.handArea) {
            return player1!!.handArea
        } else if (area === player2!!.deckArea) {
            return player1!!.deckArea
        } else if (area === player2!!.graveyardArea) {
            return player1!!.graveyardArea
        } else if (area === player2!!.removedfromgameArea) {
            return player1!!.removedfromgameArea
        } else if (area === player2!!.secretArea) {
            return player1!!.secretArea
        } else if (area === player2!!.setasideArea) {
            return player1!!.setasideArea
        }
        return null
    }
}
