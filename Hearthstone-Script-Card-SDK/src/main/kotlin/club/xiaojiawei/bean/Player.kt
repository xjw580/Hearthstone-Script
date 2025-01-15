package club.xiaojiawei.bean

import club.xiaojiawei.bean.Player.Companion.INVALID_PLAYER
import club.xiaojiawei.bean.area.*
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.mapper.PlayerMapper
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:03
 */
class Player(
    val playerId: String,
    gameId: String? = null,
    handArea: HandArea? = null,
    playArea: PlayArea? = null,
    secretArea: SecretArea? = null,
    graveyardArea: GraveyardArea? = null,
    deckArea: DeckArea? = null,
    setasideArea: SetasideArea? = null,
    removedfromgameArea: RemovedfromgameArea? = null,
    allowLog: Boolean = true
) : Entity(), Cloneable {

    @Volatile
    var gameId: String = ""
        set(value) {
            field = value
            (allowLog && this.isValid()).isTrue {
                log.info { "playerId:$playerId,gameId:$gameId" }
            }
        }
    var allowLog: Boolean

    val handArea: HandArea

    val playArea: PlayArea

    val secretArea: SecretArea

    val graveyardArea: GraveyardArea

    val deckArea: DeckArea

    val setasideArea: SetasideArea

    val removedfromgameArea: RemovedfromgameArea

    @Volatile
    var maxResources = 10

    @Volatile
    var resources = 0

    @Volatile
    var resourcesUsed = 0
        set(value) {
            (allowLog && value > 0).isTrue {
                log.info { "玩家${playerId}【${gameId}】已使用${value}法力水晶" }
            }
            field = value
        }

    /**
     * 回合开始过载水晶数（回合开始时才能获取到）
     */
    @Volatile
    var overloadLocked = 0
        set(value) {
            if (overloadLocked > 0) {
                if (resourcesUsed == 0) {
                    allowLog.isTrue {
                        log.warn { "游戏过载日志打印不规范" }
                    }
                    resources -= value
                }
                allowLog.isTrue {
                    log.info { "玩家${playerId}【${gameId}】回合开始过载${value}法力水晶" }
                }
            }
            field = value
        }

    @Volatile
    var tempResources = 0

    @Volatile
    var timeOut = 0

    @Volatile
    var turn = 0

    fun resetResources() {
        resourcesUsed = 0
        tempResources = 0
    }

    fun getArea(zoneEnum: ZoneEnum?): Area? {
        return zoneEnum?.let {
            when (it) {
                ZoneEnum.DECK -> deckArea
                ZoneEnum.HAND -> handArea
                ZoneEnum.PLAY -> playArea
                ZoneEnum.SETASIDE -> setasideArea
                ZoneEnum.SECRET -> secretArea
                ZoneEnum.GRAVEYARD -> graveyardArea
                ZoneEnum.REMOVEDFROMGAME -> removedfromgameArea
            }
        }
    }

    /**
     * 当前可用水晶数
     */
    val usableResource: Int
        get() = resources - resourcesUsed + tempResources

    companion object {
        val UNKNOWN_PLAYER: Player = Player("UNKNOWN")

        val INVALID_PLAYER: Player = Player("INVALID", "INVALID")
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return if (other is Player) {
            other.gameId == gameId && other.playerId == playerId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + playerId.hashCode()
        result = 31 * result + gameId.hashCode()
        return result
    }


    public override fun clone(): Player {
        val player = Player(
            playerId,
            handArea = handArea.deepClone(),
            playArea = playArea.deepClone(),
            secretArea = secretArea.deepClone(),
            graveyardArea = graveyardArea.deepClone(),
            deckArea = deckArea.deepClone(),
            setasideArea = setasideArea.deepClone(),
            removedfromgameArea = removedfromgameArea.deepClone(),
            allowLog = false
        )

        PlayerMapper.INSTANCE.update(this, player)

        player.handArea.player = player
        player.playArea.player = player
        player.secretArea.player = player
        player.graveyardArea.player = player
        player.deckArea.player = player
        player.setasideArea.player = player
        player.removedfromgameArea.player = player
        return player
    }

    init {
        this.allowLog = allowLog
        gameId?.let {
            this.gameId = gameId
        }
        this.handArea = handArea ?: HandArea(this)
        this.playArea = playArea ?: PlayArea(this)
        this.secretArea = secretArea ?: SecretArea(this)
        this.graveyardArea = graveyardArea ?: GraveyardArea(this)
        this.deckArea = deckArea ?: DeckArea(this)
        this.setasideArea = setasideArea ?: SetasideArea(this)
        this.removedfromgameArea = removedfromgameArea ?: RemovedfromgameArea(this)
    }
}

/**
 * 当为有效玩家时才执行block语句
 */
fun Player.safeRun(block: () -> Unit): Player {
    if (isInValid()) {
        return this
    } else {
        block()
        return this
    }
}

/**
 * 判断玩家是否有效
 */
fun Player.isValid(): Boolean {
    return this !== INVALID_PLAYER && this.gameId != "INVALID"
}

fun Player.isInValid(): Boolean {
    return this === INVALID_PLAYER || this.gameId == "INVALID"
}