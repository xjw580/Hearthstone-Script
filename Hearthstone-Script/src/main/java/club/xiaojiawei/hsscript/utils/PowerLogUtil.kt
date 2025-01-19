package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.bean.log.Block
import club.xiaojiawei.hsscript.bean.log.CommonEntity
import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.core.Core.restart
import club.xiaojiawei.hsscript.data.*
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.utils.CardUtil.exchangeAreaOfCard
import club.xiaojiawei.hsscript.utils.CardUtil.setCardAction
import club.xiaojiawei.hsscript.utils.CardUtil.updateCardByExtraEntity
import club.xiaojiawei.status.WAR
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets
import java.util.function.BiConsumer

/**
 * 解析power.log日志的工具，非常非常非常重要
 * @author 肖嘉威
 * @date 2022/11/28 23:12
 */
object PowerLogUtil {

    private val war = WAR

    /**
     * 更新entity
     * @param line
     * @param accessFile
     * @return
     */
    fun dealShowEntity(line: String, accessFile: RandomAccessFile): ExtraEntity {
        val extraEntity: ExtraEntity = parseExtraEntity(line, accessFile, SHOW_ENTITY)
        val card = war.cardMap[extraEntity.entityId]

        if (extraEntity.extraCard.zone === extraEntity.zone || extraEntity.extraCard.zone === null) {
            updateCardByExtraEntity(extraEntity, card)
        } else {
            updateCardByExtraEntity(extraEntity, card)
            exchangeAreaOfCard(extraEntity, war)
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
        if (war.cardMap[extraEntity.entityId] == null) {
            val card = Card(CommonCardAction.DEFAULT)
            updateCardByExtraEntity(extraEntity, card)
            war.cardMap[extraEntity.entityId] = card
            setCardAction(card)
            card.cardIdChangeListener = BiConsumer { oldCardId, newCardId ->
                setCardAction(card)
            }
            war.maxEntityId = card.entityId
            WarEx.getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone)
                ?.add(card, extraEntity.extraCard.zonePos)
                ?: let {
                    log.debug { "生成的card【entityId:${card.entityId}】不应没有area" }
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
        val card = war.cardMap[extraEntity.entityId]
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
                val card = war.cardMap[tagChangeEntity.entityId] ?: let {
                    log.debug { "不应找不到card,【entityId:${tagChangeEntity.entityId}】" }
                    return tagChangeEntity
                }

                //            只列出可能被修改的属性
                tagChangeEntity.tag?.tagChangeHandler?.handle(card, tagChangeEntity, war, card.area.player, card.area)

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

    fun dealBlock(line: String): Block {
        return parseBlock(line)
    }

    private fun parseBlock(line: String): Block {
        val block = Block()
        val blockTypeIndex = line.indexOf(BLOCK_TYPE)
        if (blockTypeIndex == -1) {
            return block
        }
        val entityNameIndex = line.indexOf(ENTITY, blockTypeIndex, false)
        if (entityNameIndex == -1) {
            return block
        }
        val blockType = line.substring(blockTypeIndex + BLOCK_TYPE.length + 1, entityNameIndex - 1)
        block.blockType = BlockTypeEnum.fromString(blockType)
        var commonEntity = CommonEntity()
        block.entity = CommonEntity()
//        parseCommonEntity(commonEntity, line)
        return block
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
            tagChangeEntity.entity = iso88591ToUtf8(line.substring(line.indexOf(ENTITY) + 7, tagIndex).trim())
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
        Core.lastActiveTime = System.currentTimeMillis()
        return flag
    }
}
