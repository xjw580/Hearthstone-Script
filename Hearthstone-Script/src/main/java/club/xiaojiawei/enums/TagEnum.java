package club.xiaojiawei.enums;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.Entity;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.interfaces.DealTagChange;
import club.xiaojiawei.interfaces.ParseExtraEntity;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.CardUtil;
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
    MULLIGAN_STATE("调度阶段",
            null,
            null),
    STEP("步骤",
            (card, tagChangeEntity, player, area) -> {
                War.INSTANCE.setCurrentTurnStep(StepEnum.valueOf(tagChangeEntity.getValue()));
            },
            null),
    NEXT_STEP("下一步骤",
            null,
            null),
    /*++++++++++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 游戏标签
     */
    RESOURCES("水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.INSTANCE.getCurrentPlayer() != null){
                    War.INSTANCE.getCurrentPlayer().setResources(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
    RESOURCES_USED("已使用水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.INSTANCE.getCurrentPlayer() != null){
                    War.INSTANCE.getCurrentPlayer().setResourcesUsed(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
    TEMP_RESOURCES("临时水晶数",
            (card, tagChangeEntity, player, area) -> {
                if (War.INSTANCE.getCurrentPlayer() != null){
                    War.INSTANCE.getCurrentPlayer().setTempResources(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    设置游戏id
    CURRENT_PLAYER("当前玩家",
            (card, tagChangeEntity, player, area) -> {
                if (War.INSTANCE.getMe() != null){
                    String gameId = tagChangeEntity.getEntity();
                    if (isTrue(tagChangeEntity.getValue())){
//                        匹配战网id后缀正则
                        if (!gameId.matches("^.+#\\d+$")){
                            log.warn("非正常游戏id：" + gameId);
                        }
//                        是我
                        if (Objects.equals(War.INSTANCE.getMe().getGameId(), gameId) || (War.INSTANCE.getRival().getGameId() != null && !Objects.equals(War.INSTANCE.getRival().getGameId(), gameId))){
                            War.INSTANCE.setCurrentPlayer(War.INSTANCE.getMe());
                            War.INSTANCE.getMe().resetResources();
                            War.INSTANCE.getMe().setGameId(gameId);
                        }else {
//                        是对手
                            War.INSTANCE.setCurrentPlayer(War.INSTANCE.getRival());
                            War.INSTANCE.getMe().resetResources();
                            War.INSTANCE.getRival().setGameId(gameId);
                        }
                    }
                }
            },
            null),
    FIRST_PLAYER("先手玩家",
            (card, tagChangeEntity, player, area) -> {
                String gameId = tagChangeEntity.getEntity();
                War.INSTANCE.setFirstPlayerGameId(gameId);
            },
            null),
    CARDTYPE("卡牌类型",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCardType(CARD_TYPE_MAP.getOrDefault(value, CardTypeEnum.UNKNOWN));
            }
    ),
    ZONE_POSITION("区位置",
            (card, tagChangeEntity, player, area) -> {
                card = area.removeByEntityIdInZeroArea(tagChangeEntity.getEntityId());
                area.add(card, Integer.parseInt(tagChangeEntity.getValue()));
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setZonePos(Integer.parseInt(value));
            }),
    ZONE("区域",
            (card, tagChangeEntity, player, area) -> {
                CardUtil.exchangeAreaOfCard(tagChangeEntity);
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().setZone(ZoneEnum.valueOf(value));
            }),
    PLAYSTATE("游戏状态",
            (card, tagChangeEntity, player, area) -> {
                String gameId = tagChangeEntity.getEntity();
                if (Objects.equals(tagChangeEntity.getValue(), WON)){
                    War.INSTANCE.setWon(gameId);
                }else if (Objects.equals(tagChangeEntity.getValue(), LOST)){
                    War.INSTANCE.setLost(gameId);
                }else if (Objects.equals(tagChangeEntity.getValue(), CONCEDED)){
                    War.INSTANCE.setConceded(gameId);
                }
            },
            null),
    TIMEOUT("剩余时间",
            (card, tagChangeEntity, player, area) -> {
                if (War.INSTANCE.getCurrentPlayer() != null){
                    War.INSTANCE.getCurrentPlayer().setTimeOut(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    回合结束后值改变
    TURN("自己的回合数",
            (card, tagChangeEntity, player, area) -> {
                if (Objects.equals(tagChangeEntity.getEntity(), "GameEntity")){
                    War.INSTANCE.setWarTurn(Integer.parseInt(tagChangeEntity.getValue()));
                }else if (War.INSTANCE.getCurrentPlayer() != null){
                    War.INSTANCE.getCurrentPlayer().setTurn(Integer.parseInt(tagChangeEntity.getValue()));
                }
            },
            null),
//    回合结束后值改变
    NUM_TURNS_IN_PLAY("在本局呆的回合数",
            null,
            null),
    NUM_CARDS_DRAWN_THIS_TURN("本回合抽牌数",
            null,
            null),
//    tagChange和tag里都有出现
    REVEALED("揭示",
            null,
            null),
    MAX_SLOTS_PER_PLAYER_OVERRIDE("最大槽位",
            (card, tagChangeEntity, player, area) -> {
                PlayArea playArea;
                if (Objects.equals(tagChangeEntity.getEntity(), War.INSTANCE.getMe().getGameId())){
                    playArea = War.INSTANCE.getMe().getPlayArea();
                }else {
                    playArea = War.INSTANCE.getRival().getPlayArea();
                }
                if (Objects.equals(tagChangeEntity.getValue(), "1")){
                    playArea.setOldMaxSize(playArea.getMaxSize());
                    playArea.setMaxSize(1);
                }else if (Objects.equals(tagChangeEntity.getValue(), "0")){
                    playArea.setOldMaxSize(playArea.getMaxSize());
                    playArea.setMaxSize(playArea.getDefaultMaxSize());
                }
            },
            null),
    /*++++++++++++++++++++++++++++++++++*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 卡牌属性标签-复杂TAG_CHANGE
     * {@link BaseCard}里添加
     */
    HEALTH("生命值",
            (card, tagChangeEntity, player, area) -> {
                card.setHealth(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "生命值", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setHealth(Integer.parseInt(value));
            }),
    ATK("攻击力",
            (card, tagChangeEntity, player, area) -> {
                card.setAtc(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "攻击力", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setAtc(Integer.parseInt(value));
            }),
    COST("法力值",
            (card, tagChangeEntity, player, area) -> {
                card.setCost(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "法力值", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCost(Integer.parseInt(value));
            }),
    FROZEN("冻结",
            (card, tagChangeEntity, player, area) -> {
                card.setFrozen(isTrue(tagChangeEntity.getValue()));
                log(player, card, "冻结", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setFrozen(isTrue(value));
            }),
    EXHAUSTED("疲劳",
            (card, tagChangeEntity, player, area) -> {
                card.setExhausted(isTrue(tagChangeEntity.getValue()));
                log(player, card, "疲劳", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setExhausted(isTrue(value));
            }),
    DAMAGE("受到的伤害",
            (card, tagChangeEntity, player, area) -> {
                card.setDamage(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "受到的伤害", tagChangeEntity.getValue());
            },
            null),
    TAUNT("嘲讽",
            (card, tagChangeEntity, player, area) -> {
                card.setTaunt(isTrue(tagChangeEntity.getValue()));
                log(player, card, "嘲讽", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setTaunt(isTrue(value));
            }),
    ARMOR("护甲",
            (card, tagChangeEntity, player, area) -> {
                card.setArmor(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "护甲", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setArmor(Integer.parseInt(value));
            }),
    DIVINE_SHIELD("圣盾",
            (card, tagChangeEntity, player, area) -> {
                card.setDivineShield(isTrue(tagChangeEntity.getValue()));
                log(player, card, "圣盾", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setDivineShield(isTrue(value));
            }),
    DEATHRATTLE("亡语",
            (card, tagChangeEntity, player, area) -> {
                card.setDeathRattle(isTrue(tagChangeEntity.getValue()));
                log(player, card, "亡语", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setDeathRattle(isTrue(value));
            }),
    POISONOUS("剧毒",
            (card, tagChangeEntity, player, area) -> {
                card.setPoisonous(isTrue(tagChangeEntity.getValue()));
                log(player, card, "剧毒", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setPoisonous(isTrue(value));
            }),
    AURA("光环",
            (card, tagChangeEntity, player, area) -> {
                card.setAura(isTrue(tagChangeEntity.getValue()));
                log(player, card, "光环", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setAura(isTrue(value));
            }),
    STEALTH("潜行",
            (card, tagChangeEntity, player, area) -> {
                card.setStealth(isTrue(tagChangeEntity.getValue()));
                log(player, card, "潜行", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setStealth(isTrue(value));
            }),
    WINDFURY("风怒",
            (card, tagChangeEntity, player, area) -> {
                card.setWindFury(isTrue(tagChangeEntity.getValue()));
                log(player, card, "风怒", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setWindFury(isTrue(value));
            }),
    BATTLECRY("战吼",
            (card, tagChangeEntity, player, area) -> {
                card.setBattlecry(isTrue(tagChangeEntity.getValue()));
                log(player, card, "战吼", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setBattlecry(isTrue(value));
            }),
    DISCOVER("发现",
            (card, tagChangeEntity, player, area) -> {
                card.setDiscover(isTrue(tagChangeEntity.getValue()));
                log(player, card, "发现", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setDiscover(isTrue(value));
            }),
    ADJACENT_BUFF("相邻增益",
            (card, tagChangeEntity, player, area) -> {
                card.setAdjacentBuff(isTrue(tagChangeEntity.getValue()));
                log(player, card, "相邻增益", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setAdjacentBuff(isTrue(value));
            }),
    CANT_BE_TARGETED_BY_SPELLS("不能被法术指向",
            (card, tagChangeEntity, player, area) -> {
                card.setCantBeTargetedBySpells(isTrue(tagChangeEntity.getValue()));
                log(player, card, "不能被法术指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCantBeTargetedBySpells(isTrue(value));
            }),
    CANT_BE_TARGETED_BY_HERO_POWERS("不能被英雄技能指向",
            (card, tagChangeEntity, player, area) -> {
                card.setCantBeTargetedByHeroPowers(isTrue(tagChangeEntity.getValue()));
                log(player, card, "不能被英雄技能指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCantBeTargetedByHeroPowers(isTrue(value));
            }),
    CANT_BE_TARGETED_BY_OPPONENTS("不能被对手指向",
            (card, tagChangeEntity, player, area) -> {
                card.setCantBeTargetedByOpponents(isTrue(tagChangeEntity.getValue()));
                log(player, card, "不能被对手指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCantBeTargetedByOpponents(isTrue(value));
            }),
    SPAWN_TIME_COUNT("刷出时间计数",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setSpawnTimeCount(isTrue(value));
            }),
    DORMANT_AWAKEN_CONDITION_ENCHANT("休眠状态",
            (card, tagChangeEntity, player, area) -> {
                card.setDormantAwakenConditionEnchant(isTrue(tagChangeEntity.getValue()));
                log(player, card, "休眠状态", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setDormantAwakenConditionEnchant(isTrue(value));
            }),
    ELUSIVE("无法被法术和英雄技能指向",
            (card, tagChangeEntity, player, area) -> {
                card.setElusive(isTrue(tagChangeEntity.getValue()));
                log(player, card, "无法被法术和英雄技能指向", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setElusive(isTrue(value));
            }),
    IMMUNE("免疫",
            (card, tagChangeEntity, player, area) -> {
                card.setImmune(isTrue(tagChangeEntity.getValue()));
                log(player, card, "免疫", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setImmune(isTrue(value));
            }),
    CARDRACE("种族",
            (card, tagChangeEntity, player, area) -> {
                card.setCardRace(CARD_RACE_MAP.getOrDefault(tagChangeEntity.getValue(), CardRaceEnum.UNKNOWN));
                log(player, card, "种族", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCardRace(CARD_RACE_MAP.getOrDefault(value, CardRaceEnum.UNKNOWN));
            }),
    PREMIUM("衍生物",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setPremium(isTrue(value));
            }),
    MODULAR("磁力",
            (card, tagChangeEntity, player, area) -> {
                card.setModular(isTrue(tagChangeEntity.getValue()));
                log(player, card, "磁力", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setModular(isTrue(value));
            }),
    CONTROLLER("控制者",
            (card, tagChangeEntity, player, area) -> {
                card.setController(tagChangeEntity.getValue());
                card = area.removeByEntityId(tagChangeEntity.getEntityId());
                Area reverseArea = War.INSTANCE.getReverseArea(area);
                reverseArea.add(card, 0);
                log(player, card, "控制者", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setModular(isTrue(value));
            }),
    CREATOR("创建者",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setCreator(value);
            }),
    TITAN("泰坦",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setTitan(isTrue(value));
            }),
    SPELLPOWER("法强",
            (card, tagChangeEntity, player, area) -> {
                card.setSpellPower(Integer.parseInt(tagChangeEntity.getValue()));
                log(player, card, "法强", tagChangeEntity.getValue());
            },
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setSpellPower(Integer.parseInt(value));
            }),
    DORMANT("休眠",
            null,
            (extraEntity, value) -> {
                extraEntity.getExtraCard().getCard().setDormant(isTrue(value));
            }),
    /*+++++++++++++++++++++++++++++++++++++++++++++++*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    UNKNOWN("未知",
            null,
            null),
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
            entityName = Objects.equals(Entity.UNKNOWN_ENTITY_NAME, card.getEntityName())? "" : card.getEntityName();
        }
        if (log.isDebugEnabled()){
            log.debug(String.format("【玩家%s:%s，entityId:%s，entityName:%s，cardId:%s】的【%s】发生变化:%s",
                    playerId,
                    gameId,
                    entityId,
                    entityName,
                    cardId,
                    tagComment,
                    value.toString()
            ));
        }
    }
    private static boolean isTrue(String s){
        return Objects.equals(s, "1");
    }

    private final String comment;

    private final DealTagChange dealTagChange;

    private final ParseExtraEntity parseExtraEntity;
}
