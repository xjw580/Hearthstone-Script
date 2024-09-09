package club.xiaojiawei.bean

import club.xiaojiawei.bean.area.*
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.log
import kotlin.concurrent.Volatile

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:03
 */
class Player(val playerId: String) : Entity() {

    @Volatile
    var gameId: String = ""
    set(value) {
        field = value
        log.info { "playerId:$playerId,gameId:$gameId" }
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

    fun getArea(zoneEnum: ZoneEnum): Area {
        return when (zoneEnum) {
            ZoneEnum.DECK -> deckArea
            ZoneEnum.HAND -> handArea
            ZoneEnum.PLAY -> playArea
            ZoneEnum.SETASIDE -> setasideArea
            ZoneEnum.SECRET -> secretArea
            ZoneEnum.GRAVEYARD -> graveyardArea
            ZoneEnum.REMOVEDFROMGAME -> removedfromgameArea
        }
    }

    val usableResource: Int
        get() = resources - resourcesUsed + tempResources

    companion object {
        val UNKNOWN_PLAYER: Player = Player("UNKNOWN")
    }
}
