package club.xiaojiawei.enums;

import club.xiaojiawei.custom.DealTagChange;
import club.xiaojiawei.custom.ParseExtraEntity;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.status.War;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static club.xiaojiawei.data.ScriptStaticData.*;

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:30
 */
@Slf4j
@Getter
@ToString
@AllArgsConstructor
public enum TagEnum {
    /**
     * 调度标签
     */
    MULLIGAN_STATE("MULLIGAN_STATE", "调度阶段",
            null,
            null),
    STEP("STEP", "步骤",
            (card, tagChangeEntity, player, area) -> {
                War.setCurrentTurnStep(StepEnum.valueOf(tagChangeEntity.getValue()));
            },
            null),
    NEXT_STEP("NEXT_STEP", "下一步骤",
            null,
            null),
    /*++++++++++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 游戏标签
     */
    RESOURCES("RESOURCES", "水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.getCurrentPlayer() != null){
                    War.getCurrentPlayer().setResources(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
    RESOURCES_USED("RESOURCES_USED", "已使用水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.getCurrentPlayer() != null){
                    War.getCurrentPlayer().setResourcesUsed(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
    TEMP_RESOURCES("TEMP_RESOURCES", "临时水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.getCurrentPlayer() != null){
                    War.getCurrentPlayer().setTempResources(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    设置游戏id
    CURRENT_PLAYER("CURRENT_PLAYER", "当前玩家",
            (card, tagChangeEntity, player, area) -> {
                if (War.getMe() != null){
                    String gameId = tagChangeEntity.getEntity();
                    if (isTrue(tagChangeEntity.getValue())){
                        //                匹配战网id后缀正则
                        if (gameId.matches("^.+#\\d+$")){
//                    是我
                            if (Objects.equals(War.getMe().getGameId(), gameId) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), gameId))){
                                War.setCurrentPlayer(War.getMe());
                                War.getMe().resetResources();
                                War.getMe().setGameId(gameId);
                            }else {
//                        是对手
                                War.setCurrentPlayer(War.getRival());
                                War.getMe().resetResources();
                                War.getRival().setGameId(gameId);
                            }
                        }else {
                            log.warn("非正常游戏id：" + gameId);
                        }
                    }
                }
            },
            null),
    FIRST_PLAYER("FIRST_PLAYER", "先手玩家",
            (card, tagChangeEntity, player, area) -> {
                String gameId = tagChangeEntity.getEntity();
                War.setFirstPlayerGameId(gameId);
            },
            null),
    CARDTYPE("CARDTYPE", "卡牌类型",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCardType(CARD_TYPE_MAP.getOrDefault(value, CardTypeEnum.UNKNOWN));
            }
    ),
    ZONE_POSITION("ZONE_POSITION", "区位置",
            (card, tagChangeEntity, player, area) -> {
                card = area.removeByEntityIdInZeroArea(tagChangeEntity.getEntityId());
                area.add(card, Integer.parseInt(tagChangeEntity.getValue()));
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setZonePos(Integer.parseInt(value));
            }),
    ZONE("ZONE", "区域",
            (card, tagChangeEntity, player, area) -> {
                War.exchangeAreaOfCard(tagChangeEntity);
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setZone(ZoneEnum.valueOf(value));
            }),
    PLAYSTATE("PLAYSTATE", "游戏状态",
            (card, tagChangeEntity, player, area) -> {
                String gameId = tagChangeEntity.getEntity();
                if (Objects.equals(tagChangeEntity.getValue(), WON)){
                    War.setWon(gameId);
                }else if (Objects.equals(tagChangeEntity.getValue(), LOST)){
                    War.setLost(gameId);
                }else if (Objects.equals(tagChangeEntity.getValue(), CONCEDED)){
                    War.setConceded(gameId);
                }
            },
            null),
    TIMEOUT("TIMEOUT", "剩余时间",
            (card, tagChangeEntity, player, area) -> {
                if (War.getCurrentPlayer() != null){
                    War.getCurrentPlayer().setTimeOut(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    回合结束后值改变
    TURN("TURN", "自己的回合数",
            (card, tagChangeEntity, player, area) -> {
                if (Objects.equals(tagChangeEntity.getEntity(), "GameEntity")){
                    War.setWarTurn(Integer.parseInt(tagChangeEntity.getValue()));
                }else if (War.getCurrentPlayer() != null){
                    War.getCurrentPlayer().setTurn(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    回合结束后值改变
    NUM_TURNS_IN_PLAY("NUM_TURNS_IN_PLAY", "在本局呆的回合数",
            null,
            null),
    NUM_CARDS_DRAWN_THIS_TURN("NUM_CARDS_DRAWN_THIS_TURN", "本回合抽牌数",
            null,
            null),
//    tagChange和tag里都有出现
    REVEALED("REVEALED", "揭示",
            null,
            null),
    /*++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 卡牌属性标签-复杂TAG_CHANGE
     * 和{@link Card}和{@link Card#updateByExtraEntity(ExtraEntity)}里添加
     */
    HEALTH("HEALTH", "生命值",
            (card, tagChangeEntity, player, area) -> {
                card.setHealth(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "生命值", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setHealth(Integer.parseInt(value));
            }),
    ATK("ATK", "攻击力",
            (card, tagChangeEntity, player, area) -> {
                card.setAtc(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "攻击力", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setAtc(Integer.parseInt(value));
            }),
    COST("COST", "法力值",
            (card, tagChangeEntity, player, area) -> {
                card.setCost(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "法力值", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCost(Integer.parseInt(value));
            }),
    FROZEN("FROZEN", "冻结",
            (card, tagChangeEntity, player, area) -> {
                card.setFrozen(isTrue(tagChangeEntity.getValue()));
                log(player, card, "冻结", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setFrozen(isTrue(value));
            }),
    EXHAUSTED("EXHAUSTED", "疲劳",
            (card, tagChangeEntity, player, area) -> {
                card.setExhausted(isTrue(tagChangeEntity.getValue()));
                log(player, card, "疲劳", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setExhausted(isTrue(value));
            }),
    DAMAGE("DAMAGE", "受到的伤害",
            (card, tagChangeEntity, player, area) -> {
                card.setDamage(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "受到的伤害", tagChangeEntity.getValue());
            },
            null),
    TAUNT("TAUNT", "嘲讽",
            (card, tagChangeEntity, player, area) -> {
                card.setTaunt(isTrue(tagChangeEntity.getValue()));
                log(player, card, "嘲讽", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setTaunt(isTrue(value));
            }),
    ARMOR("ARMOR", "护甲",
            (card, tagChangeEntity, player, area) -> {
                card.setArmor(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "护甲", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setArmor(Integer.parseInt(value));
            }),
    DIVINE_SHIELD("DIVINE_SHIELD", "圣盾",
            (card, tagChangeEntity, player, area) -> {
                card.setDivineShield(isTrue(tagChangeEntity.getValue()));
                log(player, card, "圣盾", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setDivineShield(isTrue(value));
            }),
    DEATHRATTLE("DEATHRATTLE", "亡语",
            (card, tagChangeEntity, player, area) -> {
                card.setDeathRattle(isTrue(tagChangeEntity.getValue()));
                log(player, card, "亡语", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setDeathRattle(isTrue(value));
            }),
    POISONOUS("POISONOUS", "剧毒",
            (card, tagChangeEntity, player, area) -> {
                card.setPoisonous(isTrue(tagChangeEntity.getValue()));
                log(player, card, "剧毒", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setPoisonous(isTrue(value));
            }),
    AURA("AURA", "光环",
            (card, tagChangeEntity, player, area) -> {
                card.setAura(isTrue(tagChangeEntity.getValue()));
                log(player, card, "光环", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setAura(isTrue(value));
            }),
    STEALTH("STEALTH", "潜行",
            (card, tagChangeEntity, player, area) -> {
                card.setStealth(isTrue(tagChangeEntity.getValue()));
                log(player, card, "潜行", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setStealth(isTrue(value));
            }),
    WINDFURY("WINDFURY", "风怒",
            (card, tagChangeEntity, player, area) -> {
                card.setWindFury(isTrue(tagChangeEntity.getValue()));
                log(player, card, "风怒", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setWindFury(isTrue(value));
            }),
    BATTLECRY("BATTLECRY", "战吼",
            (card, tagChangeEntity, player, area) -> {
                card.setBattlecry(isTrue(tagChangeEntity.getValue()));
                log(player, card, "战吼", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setBattlecry(isTrue(value));
            }),
    DISCOVER("DISCOVER", "发现",
            (card, tagChangeEntity, player, area) -> {
                card.setDiscover(isTrue(tagChangeEntity.getValue()));
                log(player, card, "发现", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setDiscover(isTrue(value));
            }),
    ADJACENT_BUFF("ADJACENT_BUFF", "相邻增益",
            (card, tagChangeEntity, player, area) -> {
                card.setAdjacentBuff(isTrue(tagChangeEntity.getValue()));
                log(player, card, "相邻增益", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setAdjacentBuff(isTrue(value));
            }),
    CANT_BE_TARGETED_BY_SPELLS("CANT_BE_TARGETED_BY_SPELLS", "不能被法术指向",
            (card, tagChangeEntity, player, area) -> {
                card.setCantBeTargetedBySpells(isTrue(tagChangeEntity.getValue()));
                log(player, card, "不能被法术指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCantBeTargetedBySpells(isTrue(value));
            }),
    CANT_BE_TARGETED_BY_HERO_POWERS("CANT_BE_TARGETED_BY_HERO_POWERS", "不能被英雄技能指向",
            (card, tagChangeEntity, player, area) -> {
                card.setCantBeTargetedByHeroPowers(isTrue(tagChangeEntity.getValue()));
                log(player, card, "不能被英雄技能指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCantBeTargetedByHeroPowers(isTrue(value));
            }),
    SPAWN_TIME_COUNT("SPAWN_TIME_COUNT", "刷出时间计数",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setSpawnTimeCount(isTrue(value));
            }),
    DORMANT_AWAKEN_CONDITION_ENCHANT("DORMANT_AWAKEN_CONDITION_ENCHANT", "休眠",
            (card, tagChangeEntity, player, area) -> {
                card.setDormantAwakenConditionEnchant(isTrue(tagChangeEntity.getValue()));
                log(player, card, "休眠", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setDormantAwakenConditionEnchant(isTrue(value));
            }),
    IMMUNE("IMMUNE", "免疫",
            (card, tagChangeEntity, player, area) -> {
                card.setImmune(isTrue(tagChangeEntity.getValue()));
                log(player, card, "免疫", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setImmune(isTrue(value));
            }),
    CARDRACE("CARDRACE", "种族",
            (card, tagChangeEntity, player, area) -> {
                card.setCardRace(CARD_RACE_MAP.getOrDefault(tagChangeEntity.getValue(), CardRaceEnum.UNKNOWN));
                log(player, card, "种族", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCardRace(CARD_RACE_MAP.getOrDefault(value, CardRaceEnum.UNKNOWN));
            }),
    PREMIUM("PREMIUM", "衍生物",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setPremium(isTrue(value));
            }),
    MODULAR("MODULAR", "磁力",
            (card, tagChangeEntity, player, area) -> {
                card.setModular(isTrue(tagChangeEntity.getValue()));
                log(player, card, "磁力", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setModular(isTrue(value));
            }),
    CONTROLLER("CONTROLLER", "控制者",
            (card, tagChangeEntity, player, area) -> {
                card.setController(tagChangeEntity.getValue());
                card = area.removeByEntityId(tagChangeEntity.getEntityId());
                Area reverseArea = War.getReverseArea(area);
                reverseArea.add(card, 0);
                log(player, card, "控制者", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setModular(isTrue(value));
            }),
    CREATOR("CREATOR", "创建者",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setCreator(value);
            }),
    TITAN("TITAN", "泰坦",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setTitan(isTrue(value));
            }),
    SPELLPOWER("SPELLPOWER", "法强",
            (card, tagChangeEntity, player, area) -> {
                card.setSpellPower(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "法强", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setSpellPower(Integer.parseInt(value));
            }),
    /*+++++++++++++++++++++++++++++++++++++++++++++++*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    UNKNOWN("UNKNOWN", "未知",
            null,
            null)
    ;
    private static void log(Player player, Card card, String tagComment, Object value){
        String playerId = "", gameId = "", entityId = "", cardId = "", entityName = "";
        if (player != null){
            playerId = player.getPlayerId();
            gameId = player.getGameId();
        }
        if (card != null){
            entityId = card.getEntityId();
            cardId = card.getCardId();
            entityName = Objects.equals(ScriptStaticData.UNKNOWN, card.getEntityName())? "" : card.getEntityName();
        }
        log.info("【玩家" + playerId + ":" + gameId + "，entityId:" + entityId+ "，entityName:" + entityName + "，cardId:" + cardId + "】的【" + tagComment + "】发生变化:" + value);
    }
    private static boolean isTrue(String s){
        return Objects.equals(s, "1");
    }

    private final String value;

    private final String comment;

    private final DealTagChange dealTagChange;

    private final ParseExtraEntity parseExtraEntity;
}
