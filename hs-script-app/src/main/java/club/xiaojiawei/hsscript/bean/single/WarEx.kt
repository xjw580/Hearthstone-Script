package club.xiaojiawei.hsscript.bean.single

import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.*
import club.xiaojiawei.bean.safeRun
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscriptbase.enums.WarPhaseEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.hsscriptbase.util.isTrue
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2024/10/11 14:41
 */
object WarEx {

    private val resetCallbackList: MutableList<Runnable> = ArrayList()

    private val endCallbackList: MutableList<Runnable> = ArrayList()

    val war = WAR

    /**
     * 已挂游戏局数
     */
    val warCountProperty: IntegerProperty = SimpleIntegerProperty(0)

    var warCount
        get() = warCountProperty.get()
        set(value) {
            warCountProperty.set(value)
            if (value > 0) {
                log.info {
                    "已完成第 $value 把游戏"
                }
            }
        }

    val inWarProperty: BooleanProperty = SimpleBooleanProperty(false)

    var inWar
        get() = inWarProperty.get()
        set(value) {
            inWarProperty.set(value)
        }

    /**
     * 本局是否胜利
     */
    var isWin = false

    /**
     * 本局获得经验
     */
    var aEXP = 0L

    /**
     * 已挂胜场
     */
    val winCountProperty: IntegerProperty = SimpleIntegerProperty(0)

    var winCount
        get() = winCountProperty.get()
        set(value) = winCountProperty.set(value)

    /**
     * 挂机时长，单位：min
     */
    val hangingTimeProperty: IntegerProperty = SimpleIntegerProperty(0)

    var hangingTime
        get() = hangingTimeProperty.get()
        set(value) = hangingTimeProperty.set(value)

    /**
     * 挂机获得的经验
     */
    val hangingEXPProperty: IntegerProperty = SimpleIntegerProperty(0)

    var hangingEXP
        get() = hangingEXPProperty.get()
        set(value) = hangingEXPProperty.set(value)


    @Synchronized
    fun resetStatistics() {
        warCount = 0
        winCount = 0
        hangingTime = 0
        hangingEXP = 0
    }

    @Synchronized
    fun reset(print: Boolean = true) {
        war.run {
            firstPlayerGameId = ""
            currentPhase = WarPhaseEnum.FILL_DECK
            currentTurnStep = null
            rival = Player.UNKNOWN_PLAYER
            me = Player.UNKNOWN_PLAYER
            currentPlayer = Player.UNKNOWN_PLAYER
            player1 = Player(allowLog = true, playerId = "1", war = war)
            player2 = Player(allowLog = true, playerId = "2", war = war)
            warTurn = 0
            conceded = ""
            lost = conceded
            won = lost
            endTime = 0
            startTime = endTime
            maxEntityId = null
            myHeroIncreaseInjury = 0
            rivalHeroIncreaseInjury = 0
        }
        isWin = false
        inWar = false
        aEXP = 0L
        print.isTrue {
            log.info { "已重置游戏状态" }
        }
        war.cardMap.clear()
        for (runnable in resetCallbackList) {
            runnable.run()
        }
        System.gc()
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
        reset(false)
        war.run {
            startTime = System.currentTimeMillis()
            currentRunMode = runModeEnum
        }
        inWar = true
    }

    @Synchronized
    fun endWar() {
        inWar = false
        war.run {
            me.safeRun {
                isWin = printResult()
            }
            endTime = if (startTime == 0L) 0 else System.currentTimeMillis()
            val time = (endTime - startTime) / 1000 / 60
            log.info { "本局游戏时长：${time}分钟" }
            hangingTime += time.toInt()
            var winExp = 0
            var lostExp = 0
            when (currentRunMode) {
                RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.CLASSIC, RunModeEnum.TWIST -> {
                    winExp = 8
                    lostExp = 6
                }

                RunModeEnum.CASUAL, RunModeEnum.BACON -> {
                    winExp = 6
                    lostExp = 4
                }

                RunModeEnum.PRACTICE -> {
                }

                else -> {
                    log.info { "未知模式，增加经验值0" }
                }
            }
            aEXP = (min(time.toDouble(), 30.0) * (if (isWin) winExp else lostExp)).toLong()
            log.info { "本局游戏获得经验值：$aEXP" }
            hangingEXP += aEXP.toInt()
            for (runnable in endCallbackList) {
                runnable.run()
            }
            warCount++
        }
    }

    private fun printResult(): Boolean {
        return war.run {
            var flag = false
            if (won == me.gameId) {
                winCount++
                flag = true
            }
            log.info { "本局游戏胜者：$won" }
            log.info { "本局游戏败者：$lost" }
            log.info { "本局游戏投降者：$conceded" }
            flag
        }
    }

    @Synchronized
    fun getPlayer(playerId: String): Player {
        return war.run {
            when (playerId) {
                player1.playerId -> player1
                player2.playerId -> player2
                else -> Player.UNKNOWN_PLAYER
            }
        }
    }

    @Synchronized
    fun getPlayerByGameId(gameId: String): Player {
        return war.run {
            when (gameId) {
                player1.gameId -> player1
                player2.gameId -> player2
                else -> Player.UNKNOWN_PLAYER
            }
        }
    }

    @Synchronized
    fun getReverseArea(area: Area): Area? {
        return war.run {
            when (area.player) {
                player1 -> player2
                player2 -> player1
                else -> null
            }
        }?.let {
            when (area) {
                is PlayArea -> {
                    return it.playArea
                }

                is HandArea -> {
                    return it.handArea
                }

                is DeckArea -> {
                    return it.deckArea
                }

                is GraveyardArea -> {
                    return it.graveyardArea
                }

                is RemovedfromgameArea -> {
                    return it.removedfromgameArea
                }

                is SecretArea -> {
                    return it.secretArea
                }

                is SetasideArea -> {
                    return it.setasideArea
                }

                else -> {
                    return null
                }
            }
        }
    }

}