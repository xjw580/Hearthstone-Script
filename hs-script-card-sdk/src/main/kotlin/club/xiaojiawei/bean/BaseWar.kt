package club.xiaojiawei.bean

import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscriptbase.enums.StepEnum
import club.xiaojiawei.hsscriptbase.enums.WarPhaseEnum

/**
 * @author 肖嘉威
 * @date 2025/1/22 16:45
 */
open class BaseWar {

    @Volatile
    open var currentPlayer: Player = Player.UNKNOWN_PLAYER

    @Volatile
    open var firstPlayerGameId: String = ""

    @Volatile
    var currentPhase = WarPhaseEnum.FILL_DECK

    @Volatile
    open var currentTurnStep: StepEnum? = null

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
     * 本局开始时间(毫秒)
     */
    @Volatile
    var startTime: Long = 0

    /**
     * 本局结束时间(毫秒)
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
     * 我方英雄增益受伤
     */
    @Volatile
    var myHeroIncreaseInjury = 0

    /**
     * 敌方英雄增益受伤
     */
    @Volatile
    var rivalHeroIncreaseInjury = 0

}