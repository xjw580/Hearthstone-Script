package club.xiaojiawei.utils;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.entity.CommonEntity;
import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.custom.DealTagChange;
import club.xiaojiawei.custom.ParseExtraEntity;
import club.xiaojiawei.bean.*;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.enums.ZoneEnum;
import club.xiaojiawei.status.War;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static club.xiaojiawei.data.ScriptStaticData.*;
import static club.xiaojiawei.enums.TagEnum.UNKNOWN;

/**
 * 解析power.log日志的工具，非常非常非常重要
 * @author 肖嘉威
 * @date 2022/11/28 23:12
 */
@Slf4j
public class PowerLogUtil {

    /**
     * 更新entity
     * @param line
     * @param accessFile
     * @return
     */
    public static ExtraEntity dealShowEntity(String line, RandomAccessFile accessFile){
        ExtraEntity extraEntity = parseExtraEntity(line, accessFile, SHOW_ENTITY);
        Card card;
        if (extraEntity.getZone() == extraEntity.getExtraCard().getZone() || extraEntity.getExtraCard().getZone() == null){
            card = CARD_AREA_MAP.get(extraEntity.getEntityId()).findByEntityId(extraEntity.getEntityId());
        }else {
            card = War.exchangeAreaOfCard(extraEntity);
        }
        card.byExtraEntityUpdate(extraEntity);
        return extraEntity;
    }

    /**
     * 生成entity
     * @param line
     * @param accessFile
     */
    public static ExtraEntity dealFullEntity(String line, RandomAccessFile accessFile){
        ExtraEntity extraEntity = parseExtraEntity(line, accessFile, FULL_ENTITY);
        //        不退出客户端的情况下断线重连会导致牌库的牌重新在日志中输出
        if (CARD_AREA_MAP.get(extraEntity.getEntityId()) == null){
            Area area;
            Card card = new Card();
            card.byExtraEntityUpdate(extraEntity);
            area = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getExtraCard().getZone());
            area.add(card, extraEntity.getExtraCard().getZonePos());
        }else {
            log.warn("dealFullEntity中发现entityId重复，将不会生成新Card");
        }
        return extraEntity;
    }

    public static TagChangeEntity dealTagChange(String line){
        TagChangeEntity tagChangeEntity = parseTagChange(line);
        tagChangeEntity.setLogType(TAG_CHANGE);
        if (tagChangeEntity.getTag() != UNKNOWN){
//        处理复杂
            if (tagChangeEntity.getEntity() == null){
                Player player = War.getPlayer(tagChangeEntity.getPlayerId());
                Area area = CARD_AREA_MAP.get(tagChangeEntity.getEntityId());
                Card card = area.findByEntityId(tagChangeEntity.getEntityId());
//            只列出可能被修改的属性
                DealTagChange dealTagChange = tagChangeEntity.getTag().getDealTagChange();
                if (dealTagChange != null){
                    dealTagChange.dealTagChange(card, tagChangeEntity, player, area);
                }
            }else {
//            处理简单
                DealTagChange dealTagChange = tagChangeEntity.getTag().getDealTagChange();
                if (dealTagChange != null) {
                    dealTagChange.dealTagChange(tagChangeEntity);
                }
            }
        }
        return tagChangeEntity;
    }

    private static TagChangeEntity parseTagChange(String line){
        int tagIndex = line.lastIndexOf(TAG);
        int valueIndex = line.lastIndexOf(VALUE);
        int index = line.lastIndexOf("]");
        TagChangeEntity tagChangeEntity = new TagChangeEntity();
        String tagName = line.substring(tagIndex + 4, valueIndex).strip();
//        为什么不用TagEnum.valueOf()?因为可能会报错
        tagChangeEntity.setTag(TAG_MAP.getOrDefault(tagName, UNKNOWN));
        tagChangeEntity.setValue(line.substring(valueIndex + 6).strip());
        if (index < 100){
            tagChangeEntity.setEntity(iso88591_To_utf8(line.substring(line.indexOf("Entity") + 7, tagIndex).strip()));
        }else {
            parseCommonEntity(tagChangeEntity, line);
        }
        return tagChangeEntity;
    }

    /**
     * 处理只有tag和value的日志
     * 如：tag=ZONE value=DECK
     * @param line
     * @param accessFile
     * @return
     */
    @SneakyThrows(value = {IOException.class})
    public static ExtraEntity parseExtraEntity(String line, RandomAccessFile accessFile, String logType){
        ExtraEntity extraEntity = new ExtraEntity();
        extraEntity.setLogType(logType);
        parseCommonEntity(extraEntity, line);
        long mark = accessFile.getFilePointer();
        int tagIndex;
        while (true){
            if ((line = accessFile.readLine()) == null){
                SystemUtil.delay(1000);
            }else if ((tagIndex = line.indexOf(TAG)) >= 0 && tagIndex < 70){
                int valueIndex = line.lastIndexOf(VALUE);
                String value = line.substring(tagIndex + 4, valueIndex - 1).strip();
                if (log.isDebugEnabled()){
                    log.debug(line);
                    log.debug("tag:"  + TAG_MAP.getOrDefault(value, UNKNOWN).getValue());
                    log.debug("extraEntity:" + extraEntity);
                }
                ParseExtraEntity parseExtraEntity = TAG_MAP.getOrDefault(value, UNKNOWN).getParseExtraEntity();
                if (parseExtraEntity != null){
                    parseExtraEntity.parseExtraEntity(extraEntity, line.substring(valueIndex + 6).strip());
                }
            }else {
                if (log.isDebugEnabled()){
                    log.debug(line);
                }
//                将指针恢复到这行日志开头，以便后面重新读取
                accessFile.seek(mark);
                break;
            }
            mark = accessFile.getFilePointer();
        }
        return extraEntity;
    }

    private static void parseCommonEntity(CommonEntity commonEntity, String line) {
        int index = line.lastIndexOf("]");
        int playerIndex = line.lastIndexOf("player", index);
        int cardIdIndex = line.lastIndexOf("cardId", playerIndex);
        int zonePosIndex = line.lastIndexOf("zonePos", cardIdIndex);
        int zoneIndex = line.lastIndexOf("zone=", zonePosIndex);
        int entityIdIndex = line.lastIndexOf("id", zoneIndex);
        int cardTypeIndex = line.lastIndexOf("cardType", entityIdIndex);
        int entityNameIndex = line.lastIndexOf("entityName", entityIdIndex);
        commonEntity.setCardId(line.substring(cardIdIndex + 7, playerIndex).strip());
        if (Strings.isBlank(commonEntity.getCardId())) {
            //noinspection AlibabaLowerCamelCaseVariableNaming
            int cardIDIndex = line.lastIndexOf("CardID");
            if (cardIDIndex != -1) {
                commonEntity.setCardId(line.substring(cardIDIndex + 7).strip());
            }
        }
        commonEntity.setPlayerId(line.substring(playerIndex + 7, index).strip());
        commonEntity.setZone(ZoneEnum.valueOf(line.substring(zoneIndex + 5, zonePosIndex).strip()));
        commonEntity.setZonePos(Integer.parseInt(line.substring(zonePosIndex + 8, cardIdIndex).strip()));
        commonEntity.setEntityId(line.substring(entityIdIndex + 3, zoneIndex).strip());
        commonEntity.setEntityName(iso88591_To_utf8(line.substring(entityNameIndex + 11, cardTypeIndex == -1? entityIdIndex : cardTypeIndex - 1).strip()));
    }

    public static String iso88591_To_utf8(String s){
        return s == null? null : new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }


    @SuppressWarnings("all")
    private static void decontamination(String path, boolean renew){
        if (renew){
            try(RandomAccessFile accessFile = new RandomAccessFile(new File(path + "\\Power.log"), "r");
                FileOutputStream outputStream = new FileOutputStream(path + "\\Power_renew.log")
            ){
                String line;
                while ((line = accessFile.readLine()) != null){
                    if (line.contains("PowerTaskList")){
                        outputStream.write(new String((line.replace("PowerTaskList.Debug", "") + "\n").getBytes(StandardCharsets.ISO_8859_1)).getBytes(StandardCharsets.UTF_8));
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            try(RandomAccessFile accessFile = new RandomAccessFile(new File(path + "\\Power.log"), "rw")){
                String line;
                long leftPoint = accessFile.getFilePointer(), rightPoint;
                while ((line = accessFile.readLine()) != null){
                    rightPoint = accessFile.getFilePointer();
                    if (line.contains("PowerTaskList")){
                        accessFile.seek(leftPoint);
                        accessFile.write(new String((line.replace("PowerTaskList.Debug", "") + "\n").getBytes(StandardCharsets.ISO_8859_1)).getBytes(StandardCharsets.UTF_8));
                        leftPoint = accessFile.getFilePointer();
                        accessFile.seek(rightPoint);
                    }
                }
                if (leftPoint != 0){
                    accessFile.setLength(leftPoint);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 将power.log日志中的不相干信息去除，方便查看
     * @param args
     */
    public static void main(String[] args) {
        decontamination("D:\\soft\\game\\Hearthstone\\Logs\\Hearthstone_2023_09_20_21_29_48", true);
    }
}
