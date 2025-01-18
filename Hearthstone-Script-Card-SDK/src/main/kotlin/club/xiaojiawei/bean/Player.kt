package club.xiaojiawei.bean

import club.xiaojiawei.bean.Player.Companion.UNKNOWN_PLAYER
import club.xiaojiawei.bean.area.*
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.mapper.AreaMapper
import club.xiaojiawei.mapper.PlayerMapper
import club.xiaojiawei.status.War
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
    war: War? = null,
    allowLog: Boolean = true
) : Entity() {

    val war: War by lazy { war ?: War.UNKNOWN_WAR }

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

    /**
     * 回合开始的水晶数
     */
    @Volatile
    var resources = 0

    /**
     * 临时水晶
     * 幸运币就是加的这个
     */
    @Volatile
    var tempResources = 0

    /**
     * 已使用的水晶
     */
    @Volatile
    var resourcesUsed = 0
        set(value) {
            (allowLog && value > 0).isTrue {
                log.info { "玩家${playerId}【${gameId}】已使用${value}法力水晶" }
            }
            field = value
        }

    /**
     * 当前可用水晶数
     */
    val usableResource: Int
        get() = resources - resourcesUsed + tempResources

    /**
     * 疲劳 todo适配
     */
    var fatigue: Int = 0

    fun incrementFatigue(): Int {
        return fatigue++
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
     * 获取当前法强
     */
    fun getSpellPower(): Int {
        return playArea.getSpellPower()
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


    fun deepClone(war: War): Player {
        val player = Player(
            playerId,
            war = war,
            allowLog = false
        )
        PlayerMapper.INSTANCE.update(this, player)

        AreaMapper.INSTANCE.update(this.handArea, player.handArea)
        this.handArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.handArea.add(newCard)
        }

        AreaMapper.INSTANCE.update(this.playArea, player.playArea)
        var newPlayCard = this.playArea.heroHide?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        newPlayCard = this.playArea.hero?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        newPlayCard = this.playArea.powerHide?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        newPlayCard = this.playArea.powerHide?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        newPlayCard = this.playArea.weaponHide?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        newPlayCard = this.playArea.weapon?.clone()
        war.cardAreaMap[newPlayCard?.entityId] = newPlayCard
        player.playArea.add(newPlayCard)
        this.playArea.cards.forEach { card ->
            player.playArea.add(card.clone())
        }

        AreaMapper.INSTANCE.update(this.secretArea, player.secretArea)
        this.secretArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.secretArea.add(newCard)
        }

        AreaMapper.INSTANCE.update(this.graveyardArea, player.graveyardArea)
        this.graveyardArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.graveyardArea.add(newCard)
        }

        AreaMapper.INSTANCE.update(this.deckArea, player.deckArea)
        this.deckArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.deckArea.add(newCard)
        }

        AreaMapper.INSTANCE.update(this.setasideArea, player.setasideArea)
        this.setasideArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.setasideArea.add(newCard)
        }

        AreaMapper.INSTANCE.update(this.removedfromgameArea, player.removedfromgameArea)
        this.removedfromgameArea.cards.forEach { card ->
            val newCard = card.clone()
            war.cardAreaMap[newCard.entityId] = newCard
            player.removedfromgameArea.add(newCard)
        }

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

    companion object {
        val UNKNOWN_PLAYER: Player = Player("UNKNOWN", "UNKNOWN")
    }
}

/**
 * 当为有效玩家时才执行block语句
 */
fun Player.safeRun(block: (Player) -> Unit): Player {
    if (isInValid()) {
        return this
    } else {
        block(this)
        return this
    }
}

/**
 * 判断玩家是否有效
 */
fun Player.isValid(): Boolean {
    return this !== UNKNOWN_PLAYER
}

fun Player.isInValid(): Boolean {
    return this === UNKNOWN_PLAYER
}