package club.xiaojiawei.hearthstone.utils;

import club.xiaojiawei.hearthstone.entity.*;
import club.xiaojiawei.hearthstone.entity.area.Area;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.phase.GameTurnAbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.GameMapConst.*;
import static club.xiaojiawei.hearthstone.constant.GameKeyWordConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static club.xiaojiawei.hearthstone.enums.TagEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/11/28 23:12
 */
@Slf4j
public class PowerLogUtil {

    public static ExtraEntity dealShowEntity(String l, RandomAccessFile accessFile){
        ExtraEntity extraEntity = PowerLogUtil.parseExtraEntity(l, accessFile);
        Card card;
        if (extraEntity.getZone() == extraEntity.getExtraCard().getZone() || extraEntity.getExtraCard().getZone() == null){
            card = CARD_AREA_MAP.get(extraEntity.getEntityId()).getByEntityId(extraEntity.getEntityId());
        }else {
            card = War.exchangeAreaOfCard(extraEntity);
        }
        card.extraEntityToCard(extraEntity);
        return extraEntity;
    }

    public static void dealFullEntity(String l, RandomAccessFile accessFile){
        ExtraEntity extraEntity = PowerLogUtil.parseExtraEntity(l, accessFile);
        Player player = War.getPlayer(extraEntity.getPlayerId());
        Card card = new Card();
        card.extraEntityToCard(extraEntity);
        Area area = player.getArea(extraEntity.getExtraCard().getZone());
        area.add(card, extraEntity.getExtraCard().getZonePos());
    }

    public static boolean dealTagChange(TagChangeEntity tagChangeEntity){
        if (tagChangeEntity.getTag() == UNKNOWN){
            return false;
        }
//        处理复杂
        if (tagChangeEntity.getEntity() == null){
            Player player = War.getPlayer(tagChangeEntity.getPlayerId());
            Area area = CARD_AREA_MAP.get(tagChangeEntity.getEntityId());
            if (area == null){
                return false;
            }
            Card card = area.getByEntityId(tagChangeEntity.getEntityId());
            switch (tagChangeEntity.getTag()){
                case ZONE_POSITION -> {
                    card = area.removeByEntityId(tagChangeEntity.getEntityId());
                    area.add(card, Integer.parseInt(tagChangeEntity.getValue()));
                }
                case ZONE -> player.getArea(ZONE_MAP.get(tagChangeEntity.getValue())).add(card, 0);
                case HEALTH -> card.setHealth(Integer.parseInt(tagChangeEntity.getValue()));
                case ATK -> card.setAtc(Integer.parseInt(tagChangeEntity.getValue()));
                case COST -> card.setCost(Integer.parseInt(tagChangeEntity.getValue()));
                case FROZEN -> card.setFrozen(Objects.equals(tagChangeEntity.getValue(), "1"));
                case EXHAUSTED -> card.setExhausted(Objects.equals(tagChangeEntity.getValue(), "1"));
                case DAMAGE -> card.setDamage(Integer.parseInt(tagChangeEntity.getValue()));
                case TAUNT -> card.setTaunt(Objects.equals(tagChangeEntity.getValue(), "1"));
                case ARMOR -> card.setArmor(Integer.parseInt(tagChangeEntity.getValue()));
                case DIVINE_SHIELD -> card.setDivineShield(Objects.equals(tagChangeEntity.getValue(), "1"));
                case POISONOUS -> card.setPoisonous(Objects.equals(tagChangeEntity.getValue(), "1"));
                case DEATHRATTLE -> card.setDeathRattle(Objects.equals(tagChangeEntity.getValue(), "1"));
                case AURA -> card.setAura(Objects.equals(tagChangeEntity.getValue(), "1"));
                case STEALTH -> card.setStealth(Objects.equals(tagChangeEntity.getValue(), "1"));
                default -> {
                    return false;
                }
            }
        }else {
//            处理简单
            switch (tagChangeEntity.getTag()){
                case RESOURCES_USED -> GameTurnAbstractPhaseStrategy.getCurrentPlayer().setUsedResources(Integer.parseInt(tagChangeEntity.getValue()));
                case RESOURCES -> GameTurnAbstractPhaseStrategy.getCurrentPlayer().setResources(Integer.parseInt(tagChangeEntity.getValue()));
                case TEMP_RESOURCES -> GameTurnAbstractPhaseStrategy.getCurrentPlayer().setTempResources(Integer.parseInt(tagChangeEntity.getValue()));
                case PLAYSTATE -> {
                    String info;
                    if (Objects.equals(tagChangeEntity.getValue(), WON)){
                        log.info(info = "本局游戏胜者：" + new String(tagChangeEntity.getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                        WebSocketServer.sendAllMessage(WsResult.ofScriptLog(info));
                    }else if (Objects.equals(tagChangeEntity.getValue(), LOST)){
                        log.info(info = "本局游戏败者：" + new String(tagChangeEntity.getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                        WebSocketServer.sendAllMessage(WsResult.ofScriptLog(info));
                    }else if (Objects.equals(tagChangeEntity.getValue(), CONCEDED)){
                        log.info(info = "本局游戏投降者：" + new String(tagChangeEntity.getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                        WebSocketServer.sendAllMessage(WsResult.ofScriptLog(info));
                    }
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    public static TagChangeEntity parseTagChange(String l){
        int tagIndex = l.lastIndexOf("tag");
        int valueIndex = l.lastIndexOf(VALUE);
        int index = l.lastIndexOf("]");
        TagChangeEntity tagChangeEntity = new TagChangeEntity();
        tagChangeEntity.setTag(TAG_MAP.getOrDefault(l.substring(tagIndex + 4, valueIndex).strip(), UNKNOWN));
        tagChangeEntity.setValue(l.substring(valueIndex + 6).strip());
        if (index == -1){
            tagChangeEntity.setEntity(l.substring(l.indexOf("Entity") + 7, tagIndex).strip());
        }else {
            parseCommonEntity(tagChangeEntity, l);
        }
        return tagChangeEntity;
    }

    public static Block parseBlock(String l){
        int index = l.indexOf("EffectCardId");
        int entityIndex = l.indexOf("Entity");
        CommonEntity commonEntity = new CommonEntity();
        commonEntity.setEntity(l.substring(entityIndex + 7, index).strip());
        Block block = new Block();
        block.setEntity(commonEntity);
        block.setBlockType(BLOCK_TYPE_MAP.get(l.substring(l.lastIndexOf("BlockType") + 10, l.lastIndexOf("Entity")).strip()));
        return block;
    }

    private static final boolean[] SIGH = new boolean[16];

    /**
     * 处理只有tag和value的日志
     * 如：tag=ZONE value=DECK
     * @param l
     * @param accessFile
     * @return
     */
    @SneakyThrows(value = {IOException.class})
    public static ExtraEntity parseExtraEntity(String l, RandomAccessFile accessFile){
        ExtraEntity extraEntity = new ExtraEntity();
        parseCommonEntity(extraEntity, l);
        long mark = accessFile.getFilePointer();
        while (true){
            if ((l = accessFile.readLine()) == null){
                ROBOT.delay(1000);
            }else if (l.lastIndexOf("    tag=") == -1){
                accessFile.seek(mark);
                Arrays.fill(SIGH, false);
                break;
            }else if (!SIGH[0] && l.lastIndexOf("CARDTYPE") != -1){
                extraEntity.getExtraCard().setCardType(CARD_TYPE_MAP.get(parseValue(l)));
                SIGH[0] = true;
            }else if (!SIGH[1] && l.lastIndexOf(COST.getValue()) != -1){
                extraEntity.getExtraCard().setCost(Integer.parseInt(parseValue(l)));
                SIGH[1] = true;
            }else if (!SIGH[2] && l.lastIndexOf(ATK.getValue()) != -1){
                extraEntity.getExtraCard().setAtc(Integer.parseInt(parseValue(l)));
                SIGH[2] = true;
            }else if (!SIGH[3] && l.lastIndexOf(HEALTH.getValue()) != -1){
                extraEntity.getExtraCard().setHealth(Integer.parseInt(parseValue(l)));
                SIGH[3] = true;
            }else if (!SIGH[4] && l.lastIndexOf(ZONE.getValue()) != -1){
                extraEntity.getExtraCard().setZone(ZONE_MAP.get(parseValue(l)));
                SIGH[4] = true;
            }else if (!SIGH[5] && l.lastIndexOf(ZONE_POSITION.getValue()) != -1){
                extraEntity.getExtraCard().setZonePos(Integer.parseInt(parseValue(l)));
                SIGH[5] = true;
            }else if (!SIGH[6] && l.lastIndexOf("ADJACENT_BUFF") != -1){
                extraEntity.getExtraCard().setAdjacentBuff("1".equals(parseValue(l)));
                SIGH[6] = true;
            }else if (!SIGH[7] && l.lastIndexOf(POISONOUS.getValue()) != -1){
                extraEntity.getExtraCard().setPoisonous("1".equals(parseValue(l)));
                SIGH[7] = true;
            }else if (!SIGH[8] && l.lastIndexOf(DEATHRATTLE.getValue()) != -1){
                extraEntity.getExtraCard().setDeathRattle("1".equals(parseValue(l)));
                SIGH[8] = true;
            }else if (!SIGH[9] && l.lastIndexOf(EXHAUSTED.getValue()) != -1){
                extraEntity.getExtraCard().setExhausted("1".equals(parseValue(l)));
                SIGH[9] = true;
            }else if (!SIGH[10] && l.lastIndexOf(FROZEN.getValue()) != -1){
                extraEntity.getExtraCard().setFrozen("1".equals(parseValue(l)));
                SIGH[10] = true;
            }else if (!SIGH[11] && l.lastIndexOf(TAUNT.getValue()) != -1){
                extraEntity.getExtraCard().setTaunt("1".equals(parseValue(l)));
                SIGH[11] = true;
            }else if (!SIGH[12] && l.lastIndexOf(ARMOR.getValue()) != -1){
                extraEntity.getExtraCard().setArmor(Integer.parseInt(parseValue(l)));
                SIGH[12] = true;
            }else if (!SIGH[13] && l.lastIndexOf(DIVINE_SHIELD.getValue()) != -1){
                extraEntity.getExtraCard().setDivineShield("1".equals(parseValue(l)));
                SIGH[13] = true;
            }else if (!SIGH[14] && l.lastIndexOf(AURA.getValue()) != -1){
                extraEntity.getExtraCard().setAura("1".equals(parseValue(l)));
                SIGH[14] = true;
            }else if (!SIGH[15] && l.lastIndexOf(STEALTH.getValue()) != -1){
                extraEntity.getExtraCard().setStealth("1".equals(parseValue(l)));
                SIGH[15] = true;
            }
            mark = accessFile.getFilePointer();
        }
        return extraEntity;
    }

    private static void parseCommonEntity(CommonEntity commonEntity, String l){
        int index = l.lastIndexOf("]");
        int playerIndex = l.lastIndexOf("player", index);
        int cardIdIndex = l.lastIndexOf("cardId", playerIndex);
        int zonePosIndex = l.lastIndexOf("zonePos", cardIdIndex);
        int zoneIndex = l.lastIndexOf("zone=", zonePosIndex);
        int entityIdIndex = l.lastIndexOf("id", zoneIndex);
        int entityNameIndex = l.lastIndexOf("entityName", entityIdIndex);
        commonEntity.setCardId(l.substring(cardIdIndex + 7, playerIndex).strip());
        if (Strings.isBlank(commonEntity.getCardId())){
            //noinspection AlibabaLowerCamelCaseVariableNaming
            int cardIDIndex = l.lastIndexOf("CardID");
            if (cardIDIndex != -1){
                commonEntity.setCardId(l.substring(cardIDIndex + 7).strip());
            }
        }
        if (Strings.isNotBlank(commonEntity.getCardId()) && commonEntity.getCardId().contains(COIN)){
            commonEntity.setCardId(COIN);
        }
        commonEntity.setPlayerId(l.substring(playerIndex + 7, index).strip());
        commonEntity.setZone(ZONE_MAP.get(l.substring(zoneIndex + 5, zonePosIndex).strip()));
        commonEntity.setZonePos(Integer.parseInt(l.substring(zonePosIndex + 8, cardIdIndex).strip()));
        commonEntity.setEntityId(l.substring(entityIdIndex + 3, zoneIndex).strip());
        commonEntity.setEntityName(l.substring(entityNameIndex + 11, entityIdIndex).strip());
    }

    public static String parseValue(String l){
        return l.substring(l.lastIndexOf(VALUE) + 6).strip();
    }
}
