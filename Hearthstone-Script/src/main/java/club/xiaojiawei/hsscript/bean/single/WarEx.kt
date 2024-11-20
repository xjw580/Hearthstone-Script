package club.xiaojiawei.hsscript.bean.single

import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.bean.area.DeckArea
import club.xiaojiawei.bean.area.GraveyardArea
import club.xiaojiawei.bean.area.HandArea
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.bean.area.RemovedfromgameArea
import club.xiaojiawei.bean.area.SecretArea
import club.xiaojiawei.bean.area.SetasideArea
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.status.War.conceded
import club.xiaojiawei.status.War.currentPhase
import club.xiaojiawei.status.War.currentPlayer
import club.xiaojiawei.status.War.currentRunMode
import club.xiaojiawei.status.War.currentTurnStep
import club.xiaojiawei.status.War.endTime
import club.xiaojiawei.status.War.firstPlayerGameId
import club.xiaojiawei.status.War.lost
import club.xiaojiawei.status.War.me
import club.xiaojiawei.status.War.player1
import club.xiaojiawei.status.War.player2
import club.xiaojiawei.status.War.rival
import club.xiaojiawei.status.War.startTime
import club.xiaojiawei.status.War.warTurn
import club.xiaojiawei.status.War.won
import club.xiaojiawei.util.isTrue
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import java.util.function.Consumer
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2024/10/11 14:41
 */
/**
 * 存放所有卡牌所在哪一区域
 */
val CARD_AREA_MAP: MutableMap<String?, Area?> = HashMap<String?, Area?>()

object WarEx {

    private val resetCallbackList: MutableList<Runnable> = ArrayList()

    private val endCallbackList: MutableList<Runnable> = ArrayList()

    /**
     * 已挂游戏局数
     */
    val warCountProperty: IntegerProperty = SimpleIntegerProperty(0)

    var warCount
        get() = warCountProperty.get()
        set(value) = warCountProperty.set(value)

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
        warCountProperty.set(0)
        winCountProperty.set(0)
        hangingTimeProperty.set(0)
        hangingEXPProperty.set(0)
    }

    @Synchronized
    fun reset(print: Boolean = true) {
        firstPlayerGameId = ""
        currentPhase = WarPhaseEnum.FILL_DECK
        currentTurnStep = null
        rival = Player.INVALID_PLAYER
        me = Player.INVALID_PLAYER
        currentPlayer = Player.INVALID_PLAYER
        player1 = Player("1")
        player2 = Player("2")
        warTurn = 0
        conceded = ""
        lost = conceded
        won = lost
        endTime = 0
        startTime = endTime
        print.isTrue {
            log.info { "已重置游戏状态" }
        }
        CARD_AREA_MAP.clear()
        resetCallbackList.forEach(Consumer { obj: Runnable -> obj.run() })
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
        startTime = System.currentTimeMillis()
        currentRunMode = runModeEnum
    }

    @Synchronized
    fun endWar() {
        var flag = false
        if (me !== Player.INVALID_PLAYER) {
            flag = printResult()
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
        val earnExp = (min(time.toDouble(), 30.0) * (if (flag) winExp else lostExp)).toLong()
        log.info { "本局游戏获得经验值：$earnExp" }
        hangingEXP += earnExp.toInt()
        endCallbackList.forEach(Consumer { obj: Runnable -> obj.run() })
        warCountProperty.set(warCountProperty.get() + 1)
    }

    private fun printResult(): Boolean {
        var flag = false
        if (won == me.gameId) {
            winCount++
            flag = true
        }
        log.info { "本局游戏胜者：$won" }
        log.info { "本局游戏败者：$lost" }
        log.info { "本局游戏投降者：$conceded" }
        return flag
    }

    @Synchronized
    fun getPlayer(playerId: String): Player {
        return when (playerId) {
            player1.playerId -> player1
            player2.playerId -> player2
            else -> Player.INVALID_PLAYER
        }
    }

    @Synchronized
    fun getReverseArea(area: Area): Area? {
        return when (area.player) {
            player1 -> player2
            player2 -> player1
            else -> null
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