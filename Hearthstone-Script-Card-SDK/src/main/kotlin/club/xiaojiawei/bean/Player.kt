package club.xiaojiawei.bean

import club.xiaojiawei.bean.Player.Companion.UNKNOWN_PLAYER
import club.xiaojiawei.bean.area.*
import club.xiaojiawei.config.ENABLE_PLAYER_LOG
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.mapper.AreaMapper
import club.xiaojiawei.mapper.PlayerMapper
import club.xiaojiawei.util.isTrue
import java.util.function.BiConsumer

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:03
 */
class Player(
    /**
     * 允许打印日志
     */
    val allowLog: Boolean = false,
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
) : Entity() {

    val war: War by lazy { war ?: War.UNKNOWN_WAR }

    @Volatile
    var gameId: String = ""
        set(value) {
            field = value
            (allowLog && this.isValid() && ENABLE_PLAYER_LOG).isTrue {
                log.info { "playerId:$playerId,gameId:$gameId" }
            }
        }

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
    var usedResources = 0
        set(value) {
            (allowLog && value > 0 && ENABLE_PLAYER_LOG).isTrue {
                log.info { "玩家${playerId}【${gameId}】已使用${value}法力水晶" }
            }
            field = value
        }

    /**
     * 当前可用水晶数
     */
    val usableResource: Int
        get() = resources - usedResources + tempResources

    /**
     * 疲劳
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
                if (usedResources == 0) {
                    (allowLog && ENABLE_PLAYER_LOG).isTrue {
                        log.warn { "游戏过载日志打印不规范" }
                    }
                    resources -= value
                }
                (allowLog && ENABLE_PLAYER_LOG).isTrue {
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
        usedResources = 0
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

    fun deepClone(newWar: War): Player {
        val newPlayer = Player(
            allowLog = newWar.allowLog,
            playerId = playerId,
            war = newWar,
        )
        PlayerMapper.INSTANCE.update(this, newPlayer)
        clonePlayArea(newPlayer, newWar)
        cloneArea(this.handArea, newPlayer.handArea, newWar)
        cloneArea(this.secretArea, newPlayer.secretArea, newWar)
        cloneArea(this.graveyardArea, newPlayer.graveyardArea, newWar)
        cloneArea(this.deckArea, newPlayer.deckArea, newWar)
        cloneArea(this.setasideArea, newPlayer.setasideArea, newWar)
        cloneArea(this.removedfromgameArea, newPlayer.removedfromgameArea, newWar)

        return newPlayer
    }

    private fun clonePlayArea(newPlayer: Player, newWar: War) {
        val newPlayArea = newPlayer.playArea
        this.playArea.heroHide?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.heroHide = it
        }
        this.playArea.hero?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.hero = it
        }
        this.playArea.powerHide?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.powerHide = it
        }
        this.playArea.power?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.power = it
        }
        this.playArea.weaponHide?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.weaponHide = it
        }
        this.playArea.weapon?.clone()?.let {
            initCloneCard(it, newPlayArea, newWar)
//            newPlayArea.weapon = it
        }
        cloneArea(this.playArea, newPlayer.playArea, newWar)
    }

    private fun initCloneCard(card: Card, newArea: Area, newWar: War) {
        val action = card.action
        newWar.addCard(card, newArea)
//        card.area = newArea
//        newWar.cardMap[card.entityId] = card
        card.damageChangeListener = BiConsumer { oldDamage, newDamage ->
            val isAlive = card.isAlive()
            if (!isAlive) {
                card.damageChangeListener = null
            }
            val diffDamage = newDamage - oldDamage
            if (diffDamage > 0) {
                action.triggerDamage(newWar, diffDamage)
            }
            if (!isAlive) {
                action.triggerDeath(newWar)
            }
        }
    }

    private fun cloneArea(sourceArea: Area, targetArea: Area, newWar: War) {
        AreaMapper.INSTANCE.update(sourceArea, targetArea)
        val cards = sourceArea.cards
        for (card in cards) {
            val newCard = card.clone()
            initCloneCard(newCard, targetArea, newWar)
//            targetArea.add(newCard)
        }
    }

    init {
        gameId?.let {
            this.gameId = gameId
        }
        this.handArea = handArea ?: HandArea(allowLog = allowLog, player = this)
        this.playArea = playArea ?: PlayArea(allowLog = allowLog, player = this)
        this.secretArea = secretArea ?: SecretArea(allowLog = allowLog, player = this)
        this.graveyardArea = graveyardArea ?: GraveyardArea(allowLog = allowLog, player = this)
        this.deckArea = deckArea ?: DeckArea(allowLog = allowLog, player = this)
        this.setasideArea = setasideArea ?: SetasideArea(allowLog = allowLog, player = this)
        this.removedfromgameArea = removedfromgameArea ?: RemovedfromgameArea(allowLog = allowLog, player = this)
    }

    companion object {
        val UNKNOWN_PLAYER: Player = Player(allowLog = false, playerId = "UNKNOWN", gameId = "UNKNOWN")
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