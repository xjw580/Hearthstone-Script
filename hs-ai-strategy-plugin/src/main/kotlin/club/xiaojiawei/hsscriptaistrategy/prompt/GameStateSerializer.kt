package club.xiaojiawei.hsscriptaistrategy.prompt

import club.xiaojiawei.hsscriptcardsdk.bean.Card
import club.xiaojiawei.hsscriptcardsdk.bean.Player
import club.xiaojiawei.hsscriptcardsdk.enums.CardTypeEnum
import club.xiaojiawei.hsscriptcardsdk.status.WAR
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GameState(
    val turn: String,
    val myHero: HeroInfo,
    val myHand: List<HandCardInfo>,
    val myBoard: List<BoardCardInfo>,
    val myWeapon: WeaponInfo?,
    val myHeroPower: HeroPowerInfo?,
    val myMana: ManaInfo,
    val myDeckCount: Int,
    val rivalHero: HeroInfo,
    val rivalHandCount: Int,
    val rivalBoard: List<BoardCardInfo>,
    val rivalHeroPower: HeroPowerInfo?,
    val rivalMana: ManaInfo,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class HeroInfo(
    val name: String,
    val health: Int,
    val armor: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class HandCardInfo(
    val index: Int,
    val name: String,
    val cardId: String,
    val cost: Int,
    val type: String,
    val atk: Int,
    val hp: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BoardCardInfo(
    val index: Int,
    val name: String,
    val cost: Int,
    val atk: Int,
    val hp: Int,
    val canAttack: Boolean,
    val keywords: List<String>,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class WeaponInfo(
    val name: String,
    val atk: Int,
    val durability: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class HeroPowerInfo(
    val name: String,
    val usable: Boolean,
    val cost: Int,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ManaInfo(
    val total: Int,
    val available: Int,
    val overloadLocked: Int = 0,
)

object GameStateSerializer {

    private val mapper = jacksonObjectMapper()

    fun serialize(): String {
        val me = WAR.me
        val rival = WAR.rival
        val state = GameState(
            turn = "my",
            myHero = heroOf(me),
            myHand = handOf(me),
            myBoard = boardOf(me),
            myWeapon = weaponOf(me),
            myHeroPower = heroPowerOf(me),
            myMana = manaOf(me),
            myDeckCount = me.deckArea.cards.size,
            rivalHero = heroOf(rival),
            rivalHandCount = rival.handArea.cards.size,
            rivalBoard = boardOf(rival),
            rivalHeroPower = heroPowerOf(rival),
            rivalMana = manaOf(rival),
        )
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(state)
    }

    private fun heroOf(player: Player): HeroInfo {
        val hero = player.playArea.hero
        return HeroInfo(
            name = hero?.getFormatEntityName() ?: "",
            health = hero?.let { (it.health - it.damage).coerceAtLeast(0) } ?: 0,
            armor = hero?.armor ?: 0,
        )
    }

    private fun handOf(player: Player): List<HandCardInfo> =
        player.handArea.cards.mapIndexed { i, c ->
            HandCardInfo(
                index = i,
                name = c.getFormatEntityName(),
                cardId = c.cardId,
                cost = c.cost,
                type = c.cardType.name,
                atk = c.atc,
                hp = c.health,
            )
        }

    private fun boardOf(player: Player): List<BoardCardInfo> =
        player.playArea.cards.mapIndexed { i, c ->
            BoardCardInfo(
                index = i,
                name = c.getFormatEntityName(),
                cost = c.cost,
                atk = c.atc,
                hp = (c.health - c.damage).coerceAtLeast(0),
                canAttack = !c.isExhausted && !c.isFrozen && c.atc > 0,
                keywords = keywordsOf(c),
            )
        }

    private fun keywordsOf(c: Card): List<String> {
        val k = mutableListOf<String>()
        if (c.isTaunt) k.add("taunt")
        if (c.isDivineShield) k.add("divine_shield")
        if (c.isPoisonous) k.add("poisonous")
        if (c.isDeathRattle) k.add("deathrattle")
        if (c.isStealth) k.add("stealth")
        if (c.isFrozen) k.add("frozen")
        if (c.isWindFury) k.add("windfury")
        if (c.isBattlecry) k.add("battlecry")
        if (c.isDiscover) k.add("discover")
        if (c.cardType === CardTypeEnum.LOCATION) k.add("location")
        return k
    }

    private fun weaponOf(player: Player): WeaponInfo? {
        val w = player.playArea.weapon ?: return null
        return WeaponInfo(
            name = w.getFormatEntityName(),
            atk = w.atc,
            durability = (w.durability - w.damage).coerceAtLeast(0),
        )
    }

    private fun heroPowerOf(player: Player): HeroPowerInfo? {
        val p = player.playArea.power ?: return null
        return HeroPowerInfo(
            name = p.getFormatEntityName(),
            usable = !p.isExhausted,
            cost = p.cost,
        )
    }

    private fun manaOf(player: Player): ManaInfo =
        ManaInfo(
            total = player.resources,
            available = player.usableResource,
            overloadLocked = 0,
        )

}
