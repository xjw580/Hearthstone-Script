package club.xiaojiawei.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CommonCardAction
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.bean.log.CommonEntity
import club.xiaojiawei.bean.log.ExtraEntity
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.config.log
import club.xiaojiawei.core.Core.restart
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.enums.TagEnum
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.starter.ExceptionListenStarter.lastActiveTime
import club.xiaojiawei.status.War.getPlayer
import club.xiaojiawei.utils.CardUtil.addAreaListener
import club.xiaojiawei.utils.CardUtil.exchangeAreaOfCard
import club.xiaojiawei.utils.CardUtil.setCardAction
import club.xiaojiawei.utils.CardUtil.updateCardByExtraEntity
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.apache.logging.log4j.util.Strings
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets

/**
 * 解析power.log日志的工具，非常非常非常重要
 * @author 肖嘉威
 * @date 2022/11/28 23:12
 */
object PowerLogUtil {

    /**
     * 更新entity
     * @param line
     * @param accessFile
     * @return
     */
    fun dealShowEntity(line: String, accessFile: RandomAccessFile): ExtraEntity {
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, ScriptStaticData.SHOW_ENTITY)

        if (extraEntity.extraCard.zone === extraEntity.zone || extraEntity.extraCard.zone === null) {
            val card = ScriptStaticData.CARD_AREA_MAP[extraEntity.entityId]?.findByEntityId(extraEntity.entityId)
            updateCardByExtraEntity(extraEntity, card)
        } else {
            val card = exchangeAreaOfCard(extraEntity)
            updateCardByExtraEntity(extraEntity, card)
        }

        return extraEntity
    }

    /**
     * 生成entity
     * @param line
     * @param accessFile
     */
    fun dealFullEntity(line: String, accessFile: RandomAccessFile): ExtraEntity {
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, ScriptStaticData.FULL_ENTITY)
        if (ScriptStaticData.CARD_AREA_MAP[extraEntity.entityId] == null) {
            val card = Card(CommonCardAction.DEFAULT)
            addAreaListener(card)
            updateCardByExtraEntity(extraEntity, card)
            setCardAction(card)
            card.cardIdProperty.addListener(ChangeListener { observableValue: ObservableValue<out String?>?, s: String?, t1: String? ->
                setCardAction(card)
            })

            getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone)
                ?.add(card, extraEntity.extraCard.zonePos)
                ?: let {
                    log.warn { "生成的card${card}未发现area" }
                }
        } else {
            if (log.isDebugEnabled()) {
                //        不退出客户端的情况下断线重连会导致牌库的牌重新在日志中输出
                log.debug { "生成的card重复，将不会生成新Card，疑似掉线重连" }
            }
        }
        return extraEntity
    }

    /**
     * 交换entity
     * @param line
     * @param accessFile
     * @return
     */
    fun dealChangeEntity(line: String, accessFile: RandomAccessFile): ExtraEntity {
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, ScriptStaticData.CHANGE_ENTITY)
        val card = ScriptStaticData.CARD_AREA_MAP[extraEntity.entityId]?.findByEntityId(extraEntity.entityId)
        log.info {
            String.format(
                "玩家%s【%s】 的 【entityId:%s】 由 【entityName:%s，cardId:%s】 变形成了 【entityName:，cardId:%s】",
                extraEntity.playerId,
                getPlayer(extraEntity.playerId).gameId,
                extraEntity.entityId,
                card?.entityName,
                card?.cardId,
                extraEntity.cardId
            )
        }
        extraEntity.entityName = ""
        updateCardByExtraEntity(extraEntity, card)
        return extraEntity
    }

    /**
     * 改变entity属性
     * @param line
     * @return
     */
    fun dealTagChange(line: String): TagChangeEntity {
        val tagChangeEntity: TagChangeEntity = parseTagChange(line)
        tagChangeEntity.logType = ScriptStaticData.TAG_CHANGE
        if (tagChangeEntity.tag !== TagEnum.UNKNOWN) {
//        处理复杂
            if (tagChangeEntity.getEntity() == null) {
                val player = getPlayer(tagChangeEntity.getPlayerId())
                val area = ScriptStaticData.CARD_AREA_MAP.get(tagChangeEntity.entityId)
                if (area == null) {
                    return tagChangeEntity
                }
                val card = area.findByEntityId(tagChangeEntity.entityId)
                //            只列出可能被修改的属性
                val dealTagChange = tagChangeEntity.tag?.fullTagChangeHandler
                if (dealTagChange != null) {
                    dealTagChange.handle(card, tagChangeEntity, player, area)
                }
                if (!tagChangeEntity.entityName.isBlank() && tagChangeEntity.entityName != Entity.UNKNOWN_ENTITY_NAME) {
                    card!!.entityName = tagChangeEntity.entityName
                }
            } else {
//            处理简单
                val dealTagChange = tagChangeEntity.tag?.tagChangeHandler
                if (dealTagChange != null) {
                    dealTagChange.handle(tagChangeEntity)
                }
            }
        }
        return tagChangeEntity
    }

    private fun parseTagChange(line: String): TagChangeEntity {
        val tagIndex = line.lastIndexOf(ScriptStaticData.TAG)
        var valueIndex = line.lastIndexOf(ScriptStaticData.VALUE)
        val index = line.lastIndexOf("]")
        val tagChangeEntity = TagChangeEntity()
        val tagName = line.substring(tagIndex + 4, valueIndex).trim()
        tagChangeEntity.setTag(ScriptStaticData.TAG_MAP.getOrDefault(tagName, TagEnum.UNKNOWN))
        var value = line.substring(valueIndex + 6).trim()
        //        可能会有这样的日志：TAG_CHANGE Entity=128 tag=DISPLAYED_CREATOR value=46 DEF CHANGE
        if ((value.indexOf(" ").also { valueIndex = it }) != -1) {
            value = value.substring(0, valueIndex)
        }
        tagChangeEntity.setValue(value)
        if (index < 100) {
            tagChangeEntity.setEntity(iso88591_To_utf8(line.substring(line.indexOf("Entity") + 7, tagIndex).trim()))
        } else {
            parseCommonEntity(tagChangeEntity, line)
        }
        return tagChangeEntity
    }

    /**
     * 处理只有tag和value的日志
     * 如：tag=ZONE value=DECK
     * @param line
     * @param accessFile
     * @return
     */
    @SneakyThrows(value = [IOException::class])
    fun parseExtraEntity(line: String, accessFile: RandomAccessFile, logType: String?): ExtraEntity {
        var line = line
        val extraEntity = ExtraEntity()
        extraEntity.setLogType(logType)
        parseCommonEntity(extraEntity, line)
        var mark = accessFile.getFilePointer()
        var tagIndex: Int
        while (true) {
            if ((accessFile.readLine().also { line = it }) == null) {
                SystemUtil.delay(1000)
            } else if ((line.indexOf(ScriptStaticData.TAG).also { tagIndex = it }) >= 0 && tagIndex < 70) {
                val valueIndex = line.lastIndexOf(ScriptStaticData.VALUE)
                val value = line.substring(tagIndex + 4, valueIndex - 1).trim()
                if (log.isDebugEnabled()) {
                    log.debug(line)
                    log.debug("tag:" + ScriptStaticData.TAG_MAP.getOrDefault(value, TagEnum.UNKNOWN).name)
                    log.debug("extraEntity:" + extraEntity)
                }
                val parseExtraEntity =
                    ScriptStaticData.TAG_MAP.getOrDefault(value, TagEnum.UNKNOWN).getParseExtraEntity()
                if (parseExtraEntity != null) {
                    parseExtraEntity.parseExtraEntity(extraEntity, line.substring(valueIndex + 6).trim())
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(line)
                }
                //                将指针恢复到这行日志开头，以便后面重新读取
                accessFile.seek(mark)
                break
            }
            mark = accessFile.getFilePointer()
        }
        return extraEntity
    }

    private fun parseCommonEntity(commonEntity: CommonEntity, line: String) {
        val index = line.lastIndexOf("]")
        val playerIndex = line.lastIndexOf("player", index)
        val cardIdIndex = line.lastIndexOf("cardId", playerIndex)
        val zonePosIndex = line.lastIndexOf("zonePos", cardIdIndex)
        val zoneIndex = line.lastIndexOf("zone=", zonePosIndex)
        val entityIdIndex = line.lastIndexOf("id", zoneIndex)
        val cardTypeIndex = line.lastIndexOf("cardType", entityIdIndex)
        val entityNameIndex = line.lastIndexOf("entityName", entityIdIndex)
        val cardIDIndex = line.lastIndexOf("CardID")
        if (cardIDIndex != -1) {
            commonEntity.cardId = line.substring(cardIDIndex + 7).trim()
        }
        if (Strings.isBlank(commonEntity.cardId)) {
            commonEntity.cardId = line.substring(cardIdIndex + 7, playerIndex).trim()
        }
        commonEntity.setPlayerId(line.substring(playerIndex + 7, index).trim())
        commonEntity.setZone(ZoneEnum.valueOf(line.substring(zoneIndex + 5, zonePosIndex).trim()))
        commonEntity.setZonePos(line.substring(zonePosIndex + 8, cardIdIndex).trim().toInt())
        commonEntity.entityId = line.substring(entityIdIndex + 3, zoneIndex).trim()
        commonEntity.entityName = PowerLogUtil.iso88591_To_utf8(
            line.substring(
                entityNameIndex + 11,
                if (cardTypeIndex == -1) entityIdIndex else cardTypeIndex - 1
            ).trim()
        )!!
    }

    fun iso88591_To_utf8(s: String?): String? {
        return if (s == null) null else String(s.toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }

    @JvmStatic
    fun isRelevance(l: String): Boolean {
        var flag = false
        if (l.contains("Truncating log")) {
            val text = "power.log达到" + (ScriptStaticData.MAX_LOG_SIZE / 1024) + "KB，游戏停止输出日志，准备重启游戏"
            log.info(text)
            SystemUtil.notice(text)
            restart()
        } else {
            flag = l.contains("PowerTaskList")
        }
        lastActiveTime = System.currentTimeMillis()
        return flag
    }
}
