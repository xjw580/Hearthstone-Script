package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.bean.log.CommonEntity
import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.bean.single.CARD_AREA_MAP
import club.xiaojiawei.hsscript.consts.CHANGE_ENTITY
import club.xiaojiawei.hsscript.consts.FULL_ENTITY
import club.xiaojiawei.hsscript.consts.MAX_LOG_SIZE_KB
import club.xiaojiawei.hsscript.consts.SHOW_ENTITY
import club.xiaojiawei.hsscript.consts.TAG
import club.xiaojiawei.hsscript.consts.TAG_CHANGE
import club.xiaojiawei.hsscript.consts.VALUE
import club.xiaojiawei.hsscript.core.Core.restart
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.starter.ExceptionListenStarter
import club.xiaojiawei.hsscript.utils.CardUtil.addAreaListener
import club.xiaojiawei.hsscript.utils.CardUtil.exchangeAreaOfCard
import club.xiaojiawei.hsscript.utils.CardUtil.setCardAction
import club.xiaojiawei.hsscript.utils.CardUtil.updateCardByExtraEntity
import javafx.beans.value.ObservableValue
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
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, SHOW_ENTITY)

        if (extraEntity.extraCard.zone === extraEntity.zone || extraEntity.extraCard.zone === null) {
            val card = CARD_AREA_MAP[extraEntity.entityId]?.findByEntityId(extraEntity.entityId)
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
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, FULL_ENTITY)
        if (CARD_AREA_MAP[extraEntity.entityId] == null) {
            val card = Card(CommonCardAction.DEFAULT)
            addAreaListener(card)
            updateCardByExtraEntity(extraEntity, card)
            setCardAction(card)
            card.cardIdProperty.addListener { _: ObservableValue<out String?>?, _: String?, _: String? ->
                setCardAction(card)
            }

            WarEx.getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone)
                ?.add(card, extraEntity.extraCard.zonePos)
                ?: let {
                    log.warn { "生成的card【entityId:${card.entityId}】不应没有area" }
                }
        } else {
            //        不退出客户端的情况下断线重连会导致牌库的牌重新在日志中输出
            log.debug { "生成的card重复，将不会生成新Card，疑似掉线重连" }
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
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, CHANGE_ENTITY)
        val card = CARD_AREA_MAP[extraEntity.entityId]?.findByEntityId(extraEntity.entityId)
        log.info {
            String.format(
                "玩家%s【%s】 的 【entityId:%s】 由 【entityName:%s，cardId:%s】 变形成了 【entityName:，cardId:%s】",
                extraEntity.playerId,
                WarEx.getPlayer(extraEntity.playerId).gameId,
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
        tagChangeEntity.logType = TAG_CHANGE

        if (tagChangeEntity.tag !== TagEnum.UNKNOWN) {
//        处理复杂，例：TAG_CHANGE Entity=[entityName=UNKNOWN ENTITY [cardType=INVALID] id=89 zone=HAND zonePos=4 cardId= player=2] tag=ZONE_POSITION value=0
            if (tagChangeEntity.entity.isBlank()) {
                val area = CARD_AREA_MAP[tagChangeEntity.entityId] ?: let {
                    log.warn { "不应找不到area,【entityId:${tagChangeEntity.entityId}】" }
                    return tagChangeEntity
                }

                val card = area.findByEntityId(tagChangeEntity.entityId) ?: let {
                    log.warn { "area不应找不到card【entityId:${tagChangeEntity.entityId}】" }
                    return tagChangeEntity
                }

                val player = WarEx.getPlayer(tagChangeEntity.playerId)
                //            只列出可能被修改的属性
                tagChangeEntity.tag?.tagChangeHandler?.handle(card, tagChangeEntity, player, area)

                if (tagChangeEntity.entityName.isNotBlank() && tagChangeEntity.entityName != Entity.UNKNOWN_ENTITY_NAME) {
                    card.entityName = tagChangeEntity.entityName
                }
            } else {
//            处理简单，例：TAG_CHANGE Entity=BouncyBear tag=NUM_TURNS_LEFT value=1
                tagChangeEntity.tag?.tagChangeHandler?.handle(tagChangeEntity)
            }
        }
        return tagChangeEntity
    }

    private fun parseTagChange(line: String): TagChangeEntity {
        val tagIndex = line.lastIndexOf(TAG)
        var valueIndex = line.lastIndexOf(VALUE)
        val index = line.lastIndexOf("]")
        val tagChangeEntity = TagChangeEntity()
        val tagName = line.substring(tagIndex + 4, valueIndex).trim()
        tagChangeEntity.tag = TagEnum.fromString(tagName)
        var value = line.substring(valueIndex + 6).trim()
        //        可能会有这样的日志：TAG_CHANGE Entity=128 tag=DISPLAYED_CREATOR value=46 DEF CHANGE
        if ((value.indexOf(" ").also { valueIndex = it }) != -1) {
            value = value.substring(0, valueIndex)
        }
        tagChangeEntity.value = value
        if (index < 100) {
            tagChangeEntity.entity = iso88591ToUtf8(line.substring(line.indexOf("Entity") + 7, tagIndex).trim())
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
    private fun parseExtraEntity(line: String, accessFile: RandomAccessFile, logType: String): ExtraEntity {
        var l = line
        val extraEntity = ExtraEntity()
        extraEntity.logType = logType
        parseCommonEntity(extraEntity, l)
        var mark = accessFile.filePointer
        var tagIndex: Int
        while (true) {
            if ((accessFile.readLine().also { l = it }) == null) {
                SystemUtil.delay(1000)
            } else if ((l.indexOf(TAG).also { tagIndex = it }) >= 0 && tagIndex < 70) {
                val valueIndex = l.lastIndexOf(VALUE)
                val value = l.substring(tagIndex + 4, valueIndex - 1).trim()
                if (log.isDebugEnabled()) {
                    log.debug { l }
                    log.debug { "tag:" + TagEnum.fromString(value).name }
                    log.debug { "extraEntity:$extraEntity" }
                }
                TagEnum.fromString(value).extraEntityHandler?.handle(extraEntity, l.substring(valueIndex + 6).trim())
            } else {
                log.debug { l }
                accessFile.seek(mark)
                break
            }
            mark = accessFile.filePointer
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

        commonEntity.apply {
            if (cardIDIndex != -1) {
                cardId = line.substring(cardIDIndex + 7).trim()
            }
            if (cardId.isBlank()) {
                cardId = line.substring(cardIdIndex + 7, playerIndex).trim()
            }
            playerId = line.substring(playerIndex + 7, index).trim()
            zone = ZoneEnum.valueOf(line.substring(zoneIndex + 5, zonePosIndex).trim())
            zonePos = line.substring(zonePosIndex + 8, cardIdIndex).trim().toInt()
            entityId = line.substring(entityIdIndex + 3, zoneIndex).trim()
            entityName = iso88591ToUtf8(
                line.substring(
                    entityNameIndex + 11,
                    if (cardTypeIndex == -1) entityIdIndex else cardTypeIndex - 1
                ).trim()
            )
        }
    }

    fun iso88591ToUtf8(s: String): String {
        return String(s.toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }

    fun isRelevance(l: String): Boolean {
        var flag = false
        if (l.contains("Truncating log")) {
            val text = "power.log达到" + (MAX_LOG_SIZE_KB) + "KB，游戏停止输出日志，准备重启游戏"
            log.info { text }
            SystemUtil.notice(text)
            restart()
        } else {
            flag = l.contains("PowerTaskList")
        }
        ExceptionListenStarter.lastActiveTime = System.currentTimeMillis()
        return flag
    }
}
