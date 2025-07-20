package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Entity
import club.xiaojiawei.bean.area.SetasideArea
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.hsscriptbase.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.bean.DiscoverCardThread
import club.xiaojiawei.hsscript.bean.FixedSizeStack
import club.xiaojiawei.hsscript.bean.log.Block
import club.xiaojiawei.hsscript.bean.log.CommonEntity
import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.config.DRIVER_LOCK
import club.xiaojiawei.hsscript.consts.*
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.core.Core.restart
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.strategy.AbstractPhaseStrategy
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator
import club.xiaojiawei.hsscript.utils.CardUtil.exchangeAreaOfCard
import club.xiaojiawei.hsscript.utils.CardUtil.setCardAction
import club.xiaojiawei.hsscript.utils.CardUtil.updateCardByExtraEntity
import club.xiaojiawei.status.WAR
import club.xiaojiawei.hsscriptbase.util.isTrue
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

/**
 * 解析power.log日志的工具，非常非常非常重要
 * @author 肖嘉威
 * @date 2022/11/28 23:12
 */
object PowerLogUtil {

    private val war = WAR

    private val blockStack: FixedSizeStack<Block> = FixedSizeStack(20)

    private val fullCardStack: FixedSizeStack<Card> = FixedSizeStack(10)

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
//            生成卡牌
            val card = Card(CommonCardAction.DEFAULT).apply {
                fullCardStack.push(this)
                updateCardByExtraEntity(extraEntity, this)
                war.cardMap[extraEntity.entityId] = this
                setCardAction(this)
                cardIdChangeListener = BiConsumer { oldCardId, newCardId ->
                    setCardAction(this)
                }
                war.maxEntityId = entityId
            }

            WarEx.getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone)
                ?.add(card, extraEntity.extraCard.zonePos)
                ?: let {
                    log.debug { "生成的card【entityId:${card.entityId}】不应没有area" }
                }
            val creator = card.creator
            if (creator.isNotEmpty()) {
                war.cardMap[creator]?.child?.add(card) ?: let {
                    log.debug { "找不到creator:${card.creator}" }
                }
            }

            dealTriggerChoose()
        } else {
//        不退出客户端的情况下断线重连会导致牌库的牌重新在日志中输出
            log.debug { "生成的card重复，将不会生成新Card，疑似掉线重连" }
        }
        return extraEntity
    }

    private var chooseFuture: Future<*>? = null

    private fun dealTriggerChoose() {
        if (fullCardStack.size() < 3 && war.isMyTurn) return
        blockStack.peek()?.let { block ->
            if (block.blockType !== BlockTypeEnum.POWER && block.blockType !== BlockTypeEnum.UNKNOWN) return
            val testChooseCard: (Card) -> Boolean = { testCard ->
                val blockEntity = block.entity
                testCard.area::class.java === SetasideArea::class.java && testCard.creator.isNotEmpty() && (blockEntity == null || testCard.creator == blockEntity.entityId)
            }

            var creator: String? = null
            val chooseCards = mutableListOf<Card>()
            val cards = fullCardStack.toList()
            for (i in cards.indices.reversed()) {
                val chooseCard = cards[i]
                if (creator == null) {
                    creator = chooseCard.creator
                } else if (chooseCard.creator != creator) {
                    break
                }
                if (testChooseCard(chooseCard)) {
                    chooseCards.addFirst(chooseCard)
                } else {
                    break
                }
            }
            chooseCards.removeAll { it.isNightmareBonus }
            if (chooseCards.isNotEmpty()) {
                chooseFuture?.cancel(true)
                chooseFuture = EXTRA_THREAD_POOL.schedule({
                    WarEx.war.isChooseCardTime = true
                    log.info { "发现卡牌：${chooseCards}" }
                    (DiscoverCardThread {
                        try {
                            if (chooseCards.size == 1) {
                                GameUtil.chooseDiscoverCard(1, 3)
                            } else {
                                DeckStrategyActuator.discoverChooseCard(chooseCards)
                            }
                        } finally {
//                            让其他线程的鼠标任务不会被抛弃，但是堵塞他们的执行
                            WarEx.war.isChooseCardTime = false
                            try {
                                DRIVER_LOCK.lock()
                                SystemUtil.delay(2000)
                            } finally {
                                DRIVER_LOCK.unlock()
                            }
                        }
                    }.also { AbstractPhaseStrategy.addTask(it) }).start()

                }, 1500, TimeUnit.MILLISECONDS)
            }
        }
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
                    log.info { "不应找不到card,【entityId:${tagChangeEntity.entityId}】" }
                    return tagChangeEntity
                }

                //            只列出可能被修改的属性
                tagChangeEntity.tag?.tagChangeHandler?.handle(card, tagChangeEntity, war, card.area.player, card.area)

                if (tagChangeEntity.entityName.isNotBlank() && Entity.isNotUnknownEntityName(tagChangeEntity.entityName)) {
                    card.entityName = tagChangeEntity.entityName
                }
            } else {
//            处理简单，例：TAG_CHANGE Entity=BouncyBear tag=NUM_TURNS_LEFT value=1
                val player = WarEx.getPlayerByGameId(tagChangeEntity.entity)
                if (player.isValid()) {
                    tagChangeEntity.tag?.tagChangeHandler?.handle(null, tagChangeEntity, war, player, null)
                } else {
                    tagChangeEntity.tag?.tagChangeHandler?.handle(tagChangeEntity)
                }
            }
        }
        return tagChangeEntity
    }

    fun dealBlock(line: String): Block {
        val block = parseBlock(line)
        block.parentBlock = blockStack.peek()
//        formatBlockLog(block)?.let {
//            log.info { it }
//        }
        blockStack.push(block)
        return block
    }

    private fun formatBlockLog(block: Block): String? {
        val entity = block.entity ?: return null
        val player = WarEx.getPlayer(entity.playerId)
        player.isValid().isTrue {
            val typeText = block.blockType.comment
            return String.format(
                "玩家%s%s【%s】，entityId:%s，entityName:%s，cardId:%s",
                player.playerId,
                if (player.gameId.isBlank()) "" else "【${player.gameId}】",
                typeText,
                entity.entityId,
                entity.getFormatEntityName(),
                entity.cardId,
            )
        }
        return null
    }

    fun dealBlockEnd(line: String): Block? {
        return blockStack.pop()
    }

    private fun parseBlock(line: String): Block {
        val block = Block()
        val blockTypeIndex = line.indexOf(BLOCK_TYPE)
        if (blockTypeIndex != -1) {
            val entityNameIndex = line.indexOf(club.xiaojiawei.hsscript.consts.ENTITY, blockTypeIndex, false)
            if (entityNameIndex == -1) {
                return block
            }
            val blockType = line.substring(blockTypeIndex + BLOCK_TYPE.length + 1, entityNameIndex - 1)
            block.blockType = BlockTypeEnum.fromString(blockType)
            val commonEntity = CommonEntity()
            block.entity = commonEntity
            parseCommonEntity(commonEntity, line)
        }
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
            tagChangeEntity.entity =
                iso88591ToUtf8(
                    line.substring(line.indexOf(club.xiaojiawei.hsscript.consts.ENTITY) + 7, tagIndex).trim()
                )
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

    private fun parseCommonEntityBack(commonEntity: CommonEntity, line: String) {
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

    private const val ENTITY = "Entity="
    private const val PRE_ENTITY = "[entity"
    private const val ENTITY_NAME = "entityName="
    private const val ID = "id="
    private const val ZONE = "zone="
    private const val ZONE_POS = "zonePos="
    private const val CARD_ID = "cardId="
    private const val CARD_ID_U = "CardID="
    private const val PLAYER = "player="

    fun parseCommonEntity(commonEntity: CommonEntity, line: String) {
        val preEntityIndex = line.indexOf(PRE_ENTITY)

        if (preEntityIndex == -1) {
            // Entity=GameEntity
            val startI = line.indexOf(ENTITY) + ENTITY.length
            for (i in startI until line.length) {
                if (line[i] == ' ') {
                    commonEntity.entity = line.substring(startI, i)
                    return
                }
            }
            // 如果没有空格，直接取到末尾
            commonEntity.entity = line.substring(startI)
        } else {
            // Entity=[entityName=吉安娜的礼物 id=13 zone=HAND zonePos=4 cardId=GIFT_02 player=1]
            // Entity=[entityName=吉安娜的礼物 id=13 zone=HAND zonePos=4 cardId=GIFT_02 player=1] CardID=CS2_024
//            [entityName=UNKNOWN ENTITY [cardType=INVALID] id=4 zone=DECK zonePos=0 cardId= player=1]
            val entityNameIndex = line.indexOf(ENTITY_NAME, preEntityIndex)
            val idIndex = line.indexOf(ID, entityNameIndex)
            val zoneIndex = line.indexOf(ZONE, idIndex)
            val zonePosIndex = line.indexOf(ZONE_POS, zoneIndex)
            val cardIdIndex = line.indexOf(CARD_ID, zonePosIndex)
            val playerIndex = line.indexOf(PLAYER, cardIdIndex)
            val endIndex = line.indexOf(']', playerIndex)
            val cardIdUIndex = line.indexOf(CARD_ID_U, endIndex)

            // 解析 entityName
            commonEntity.entityName =
                iso88591ToUtf8(line.substring(entityNameIndex + ENTITY_NAME.length, idIndex - 1).trim())

            // 解析 id
            commonEntity.entityId = line.substring(idIndex + ID.length, zoneIndex - 1).trim()

            // 解析 zone
            commonEntity.zone = ZoneEnum.valueOf(line.substring(zoneIndex + ZONE.length, zonePosIndex - 1).trim())

            // 解析 zonePos
            commonEntity.zonePos = line.substring(zonePosIndex + ZONE_POS.length, cardIdIndex - 1).trim().toInt()

            // 解析 cardId
            if (cardIdUIndex != -1) {
                commonEntity.cardId = line.substring(cardIdUIndex + CARD_ID_U.length).trim()
            }
            if (commonEntity.cardId.isEmpty()) {
                commonEntity.cardId = line.substring(cardIdIndex + CARD_ID.length, playerIndex - 1).trim()
            }

            // 解析 player
            commonEntity.playerId = line.substring(playerIndex + PLAYER.length, endIndex).trim()
        }
    }


    fun iso88591ToUtf8(s: String): String {
        return String(s.toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }

    fun isRelevance(l: String): Boolean {
        var flag = false
        if (l.contains("Truncating log")) {
            val text = "${GAME_WAR_LOG_NAME}达到" + (ScriptStatus.maxLogSizeKB) + "KB，游戏停止输出日志，准备重启游戏"
            log.info { text }
            SystemUtil.notice(text)
            restart()
        } else {
            flag = l.contains("PowerTaskList")
        }
        Core.lastActiveTime = System.currentTimeMillis()
        return flag
    }

    fun formatLogFile(logDir: String, renew: Boolean): File? {
        val sourceFile = File("$logDir\\${GAME_WAR_LOG_NAME}")
        var res: File? = null
        if (sourceFile.exists()) {
            val newFile = File("$logDir\\renew_${GAME_WAR_LOG_NAME}")
            runCatching {
                BufferedReader(FileReader(sourceFile)).use { reader ->
                    BufferedWriter(FileWriter(newFile)).use { writer ->
                        reader.lineSequence()
                            .filter { it.contains("PowerTaskList") }
                            .map { it.replace("PowerTaskList.Debug", "") + "\n" }
                            .forEach { writer.write(it) }
                    }
                }
            }.onFailure {
                log.error { it }
            }.onSuccess {
                if (renew) {
                    res = newFile
                } else {
                    newFile.renameTo(sourceFile)
                    res = sourceFile
                }
            }
        } else {
            log.error { "${sourceFile}不存在" }
        }
        return res
    }
}