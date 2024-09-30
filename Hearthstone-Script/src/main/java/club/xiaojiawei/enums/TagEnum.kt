package club.xiaojiawei.enums

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.bean.log.ExtraEntity
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.config.log
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.interfaces.TagChangeHandler
import club.xiaojiawei.interfaces.ExtraEntityHandler
import club.xiaojiawei.status.War
import club.xiaojiawei.util.isTrue
import club.xiaojiawei.utils.CardUtil

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:30
 */
enum class TagEnum(
    val comment: String = "",
    val tagChangeHandler: TagChangeHandler? = null,
    val extraEntityHandler: ExtraEntityHandler? = null
) {
    /**
     * 调度标签
     */
    MULLIGAN_STATE(
        "调度阶段",
        null,
        null
    ),
    STEP(
        "步骤",
        tagChangeHandler = { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.currentTurnStep = StepEnum.valueOf(tagChangeEntity.value)
        },
        null
    ),
    NEXT_STEP(
        "下一步骤",
        null,
        null
    ),  /*++++++++++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * 游戏标签
     */
    RESOURCES(
        "水晶数",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.currentPlayer.resources = tagChangeEntity.value.toInt()
        },
        null
    ),
    RESOURCES_USED(
        "已使用水晶数",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.currentPlayer.resourcesUsed = tagChangeEntity.value.toInt()
        },
        null
    ),
    TEMP_RESOURCES(
        "临时水晶数",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.currentPlayer.tempResources = tagChangeEntity.value.toInt()
        },
        null
    ),

    //    设置游戏id
    CURRENT_PLAYER(
        "当前玩家",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.me.isValid().isTrue {
                val gameId = tagChangeEntity.entity
                if (isTrue(tagChangeEntity.value)) {
//                        匹配战网id后缀正则
                    if (!gameId.matches("^.+#\\d+$".toRegex())) {
                        log.warn("非正常游戏id：" + gameId)
                    }

                    //                        是我
                    if (War.me.gameId == gameId
                        || (!War.rival.gameId.isBlank() && War.rival.gameId != gameId)
                    ) {
                        War.currentPlayer = War.me
                        War.me.resetResources()
                        War.me.gameId = gameId
                    } else {
//                        是对手
                        War.currentPlayer = War.rival
                        War.me.resetResources()
                        War.rival.gameId = gameId
                    }
                }
            }
        },
        null
    ),
    FIRST_PLAYER(
        "先手玩家",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            val gameId = tagChangeEntity.entity
            War.firstPlayerGameId = gameId
        },
        null
    ),
    CARDTYPE("卡牌类型",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.cardType =
                ScriptStaticData.CARD_TYPE_MAP.getOrDefault(value, CardTypeEnum.UNKNOWN)
        }
    ),
    ZONE_POSITION("区位置",
        TagChangeHandler { _: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            area ?: return@TagChangeHandler
            val card = area.removeByEntityIdInZeroArea(tagChangeEntity.entityId)
            area.add(card, tagChangeEntity.value.toInt())
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.zonePos = value.toInt()
        }),
    ZONE("区域",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            CardUtil.exchangeAreaOfCard(tagChangeEntity)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.zone = ZoneEnum.valueOf(value)
        }),
    PLAYSTATE(
        "游戏状态",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            val gameId = tagChangeEntity.entity
            if (tagChangeEntity.value == ScriptStaticData.WON) {
                War.won = gameId
            } else if (tagChangeEntity.value == ScriptStaticData.LOST) {
                War.lost = gameId
            } else if (tagChangeEntity.value == ScriptStaticData.CONCEDED) {
                War.conceded = gameId
            }
        },
        null
    ),
    TIMEOUT(
        "剩余时间",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            War.currentPlayer.timeOut = tagChangeEntity.value.toInt()
        },
        null
    ),

    //    回合结束后值改变
    TURN(
        "自己的回合数",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            if (tagChangeEntity.entity == "GameEntity") {
                War.warTurn = tagChangeEntity.value.toInt()
            } else {
                War.currentPlayer.turn = tagChangeEntity.value.toInt()
            }
        },
        null
    ),

    //    回合结束后值改变
    NUM_TURNS_IN_PLAY(
        "在本局呆的回合数",
        null,
        null
    ),
    NUM_CARDS_DRAWN_THIS_TURN(
        "本回合抽牌数",
        null,
        null
    ),

    //    tagChange和tag里都有出现
    REVEALED(
        "揭示",
        null,
        null
    ),
    MAX_SLOTS_PER_PLAYER_OVERRIDE(
        "最大槽位",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            var playArea = if (tagChangeEntity.entity == War.me.gameId) {
                War.me.playArea
            } else {
                War.rival.playArea
            }
            if (tagChangeEntity.value == "1") {
                playArea.oldMaxSize = playArea.maxSize
                playArea.maxSize = 1
            } else if (tagChangeEntity.value == "0") {
                playArea.oldMaxSize = playArea.maxSize
                playArea.maxSize = playArea.defaultMaxSize
            }
        },
        null
    ),
    /*++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * 卡牌属性标签-复杂TAG_CHANGE
     * [BaseCard]里添加
     */
    HEALTH("生命值",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.health = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "生命值", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.health = value.toInt()
        }),
    ATK("攻击力",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.atc = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "攻击力", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.atc = value.toInt()
        }),
    COST("法力值",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.cost = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "法力值", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.cost = value.toInt()
        }),
    FROZEN("冻结",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isFrozen = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "冻结", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isFrozen = TagEnum.Companion.isTrue(value)
        }),
    EXHAUSTED("疲劳",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isExhausted = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "疲劳", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isExhausted = TagEnum.Companion.isTrue(value)
        }),
    DAMAGE(
        "受到的伤害",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.damage = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "受到的伤害", tagChangeEntity.value)
        },
        null
    ),
    TAUNT("嘲讽",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isTaunt = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "嘲讽", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isTaunt = TagEnum.Companion.isTrue(value)
        }),
    ARMOR("护甲",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.armor = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "护甲", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.armor = value.toInt()
        }),
    DIVINE_SHIELD("圣盾",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isDivineShield = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "圣盾", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isDivineShield = TagEnum.Companion.isTrue(value)
        }),
    DEATHRATTLE("亡语",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isDeathRattle = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "亡语", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isDeathRattle = TagEnum.Companion.isTrue(value)
        }),
    POISONOUS("剧毒",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isPoisonous = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "剧毒", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isPoisonous = TagEnum.Companion.isTrue(value)
        }),
    AURA("光环",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isAura = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "光环", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isAura = TagEnum.Companion.isTrue(value)
        }),
    STEALTH("潜行",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isStealth = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "潜行", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isStealth = TagEnum.Companion.isTrue(value)
        }),
    WINDFURY("风怒",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isWindFury = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "风怒", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isWindFury = TagEnum.Companion.isTrue(value)
        }),
    BATTLECRY("战吼",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isBattlecry = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "战吼", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isBattlecry = TagEnum.Companion.isTrue(value)
        }),
    DISCOVER("发现",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isDiscover = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "发现", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isDiscover = TagEnum.Companion.isTrue(value)
        }),
    ADJACENT_BUFF("相邻增益",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isAdjacentBuff = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "相邻增益", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isAdjacentBuff = TagEnum.Companion.isTrue(value)
        }),
    CANT_BE_TARGETED_BY_SPELLS("不能被法术指向",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isCantBeTargetedBySpells = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "不能被法术指向", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isCantBeTargetedBySpells = TagEnum.Companion.isTrue(value)
        }),
    CANT_BE_TARGETED_BY_HERO_POWERS("不能被英雄技能指向",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isCantBeTargetedByHeroPowers = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "不能被英雄技能指向", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isCantBeTargetedByHeroPowers = TagEnum.Companion.isTrue(value)
        }),
    CANT_BE_TARGETED_BY_OPPONENTS("不能被对手指向",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isCantBeTargetedByOpponents = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "不能被对手指向", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isCantBeTargetedByOpponents = TagEnum.Companion.isTrue(value)
        }),
    SPAWN_TIME_COUNT("刷出时间计数",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isSpawnTimeCount = TagEnum.Companion.isTrue(value)
        }),
    DORMANT_AWAKEN_CONDITION_ENCHANT("休眠状态",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isDormantAwakenConditionEnchant = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "休眠状态", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isDormantAwakenConditionEnchant = TagEnum.Companion.isTrue(value)
        }),
    ELUSIVE("扰魔",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isElusive = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "扰魔", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isElusive = TagEnum.Companion.isTrue(value)
        }),
    IMMUNE("免疫",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isImmune = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "免疫", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isImmune = TagEnum.Companion.isTrue(value)
        }),
    CARDRACE("种族",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.cardRace = ScriptStaticData.CARD_RACE_MAP.getOrDefault(tagChangeEntity.value, CardRaceEnum.UNKNOWN)
            TagEnum.Companion.log(player, card, "种族", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.cardRace =
                ScriptStaticData.CARD_RACE_MAP.getOrDefault(value, CardRaceEnum.UNKNOWN)
        }),
    PREMIUM("衍生物",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isPremium = TagEnum.Companion.isTrue(value)
        }),
    MODULAR("磁力",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isModular = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "磁力", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isModular = TagEnum.Companion.isTrue(value)
        }),
    CONTROLLER("控制者",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            var card = card
            card.controller = tagChangeEntity.value
            card = area.removeByEntityId(tagChangeEntity.entityId)
            val reverseArea = War.getReverseArea(area)
            reverseArea.add(card, 0)
            TagEnum.Companion.log(player, card, "控制者", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isModular = TagEnum.Companion.isTrue(value)
        }),
    CREATOR("创建者",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.creator = value
        }),
    TITAN("泰坦",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isTitan = TagEnum.Companion.isTrue(value)
        }),
    SPELLPOWER("法强",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.spellPower = tagChangeEntity.value.toInt()
            TagEnum.Companion.log(player, card, "法强", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.spellPower = value.toInt()
        }),
    DORMANT("休眠",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isDormant = TagEnum.Companion.isTrue(value)
        }),
    ATTACKABLE_BY_RUSH("突袭攻击",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isAttackableByRush = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "突袭攻击", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isAttackableByRush = TagEnum.Companion.isTrue(value)
        }),
    IMMUNE_WHILE_ATTACKING("攻击时免疫",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isImmuneWhileAttacking = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "攻击时免疫", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isImmuneWhileAttacking = TagEnum.Companion.isTrue(value)
        }),
    REBORN("复生",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isReborn = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "复生", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isReborn = TagEnum.Companion.isTrue(value)
        }),
    TRIGGER_VISUAL("视觉触发",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isTriggerVisual = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "视觉触发", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isTriggerVisual = TagEnum.Companion.isTrue(value)
        }),
    LIFESTEAL("吸血",
        TagChangeHandler { card: Card?, tagChangeEntity: TagChangeEntity, player: Player?, area: Area? ->
            card.isLifesteal = TagEnum.Companion.isTrue(tagChangeEntity.value)
            TagEnum.Companion.log(player, card, "吸血", tagChangeEntity.value)
        },
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isLifesteal = TagEnum.Companion.isTrue(value)
        }),
    COIN_CARD("硬币",
        null,
        ExtraEntityHandler { extraEntity: ExtraEntity, value: String ->
            extraEntity.extraCard.card.isCoinCard = TagEnum.Companion.isTrue(value)
        }),

    /*+++++++++++++++++++++++++++++++++++++++++++++++*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    UNKNOWN(
        "未知",
        null,
        null
    ),
    ;


    companion object {

        private fun log(player: Player?, card: Card?, tagComment: String, value: Any) {
            var playerId = ""
            var gameId = ""
            var entityId = ""
            var cardId = ""
            var entityName = ""
            if (player != null) {
                playerId = player.playerId
                gameId = player.gameId
            }
            if (card != null) {
                entityId = card.entityId
                cardId = card.cardId
                entityName = if (Entity.UNKNOWN_ENTITY_NAME == card.entityName) "" else card.entityName
            }
            if (log.isDebugEnabled()) {
                log.debug {
                    String.format(
                        "【玩家%s:%s，entityId:%s，entityName:%s，cardId:%s】的【%s】发生变化:%s",
                        playerId,
                        gameId,
                        entityId,
                        entityName,
                        cardId,
                        tagComment,
                        value.toString()
                    )
                }
            }
        }

        private fun isTrue(s: String): Boolean {
            return s == "1"
        }
    }
}
