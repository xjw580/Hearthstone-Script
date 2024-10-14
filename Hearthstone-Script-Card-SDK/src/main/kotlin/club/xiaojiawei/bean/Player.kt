package club.xiaojiawei.bean

import club.xiaojiawei.bean.Player.Companion.INVALID_PLAYER
import club.xiaojiawei.bean.area.*
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:03
 */
class Player(val playerId: String) : Entity() {

    constructor(playerId: String, gameId: String) : this(playerId) {
        this.gameId = gameId
    }

    @Volatile
    var gameId: String = ""
        set(value) {
            field = value
            this.isValid().isTrue {
                log.info { "playerId:$playerId,gameId:$gameId" }
            }
        }

    val handArea = HandArea(this)

    val playArea = PlayArea(this)

    val secretArea = SecretArea(this)

    val graveyardArea = GraveyardArea(this)

    val deckArea = DeckArea(this)

    val setasideArea = SetasideArea(this)

    val removedfromgameArea = RemovedfromgameArea(this)

    @Volatile
    var maxResources = 10

    @Volatile
    var resources = 0

    @Volatile
    var resourcesUsed = 0

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
}

fun Player.safeRun(block: () -> Unit): Player {
    if (this === INVALID_PLAYER) {
        return this
    } else {
        block()
        return this
    }
}

fun Player.isValid(): Boolean {
    return this != INVALID_PLAYER && this.gameId != "INVALID"
}

