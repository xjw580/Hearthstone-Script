package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.bean.CommonCardAction.Companion.DEFAULT
import club.xiaojiawei.hsscript.bean.WeightCard
import club.xiaojiawei.hsscript.bean.log.ExtraEntity
import club.xiaojiawei.hsscript.bean.log.TagChangeEntity
import club.xiaojiawei.hsscript.bean.single.CARD_AREA_MAP
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.data.WEIGHT_CONFIG_PATH
import club.xiaojiawei.hsscript.status.CardActionManager.CARD_ACTION_MAP
import club.xiaojiawei.hsscript.strategy.DeckStrategyActuator
import club.xiaojiawei.mapper.BaseCardMapper
import club.xiaojiawei.mapper.EntityMapper
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.beans.value.ObservableValue
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/6 21:07
 */
object CardUtil {

    fun addAreaListener(card: Card?) {
        card?.let {
            card.areaProperty.addListener { _: ObservableValue<out Area?>?, _: Area?, newArea: Area? ->
                CARD_AREA_MAP.remove(card.entityId)
                CARD_AREA_MAP[card.entityId] = newArea
            }
        }
    }

    fun updateCardByExtraEntity(extraEntity: ExtraEntity, card: Card?) {
        card?.let {
            BaseCardMapper.INSTANCE.update(extraEntity.extraCard.card, card)
            EntityMapper.INSTANCE.update(extraEntity, card)
        }
    }

    fun exchangeAreaOfCard(extraEntity: ExtraEntity): Card? {
        val sourceArea = CARD_AREA_MAP[extraEntity.entityId] ?: let {
            WarEx.getPlayer(extraEntity.playerId).getArea(extraEntity.zone)
        } ?: return null
        val targetArea = WarEx.getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone) ?: return null

        val card = sourceArea.removeByEntityId(extraEntity.entityId)
        targetArea.add(card, extraEntity.extraCard.zonePos)

        return card
    }

    fun exchangeAreaOfCard(tagChangeEntity: TagChangeEntity): Card? {
        val sourceArea = CARD_AREA_MAP[tagChangeEntity.entityId] ?: return null
        val targetArea =
            WarEx.getPlayer(tagChangeEntity.playerId).getArea(ZoneEnum.valueOf(tagChangeEntity.value)) ?: return null

        val card = sourceArea.removeByEntityId(tagChangeEntity.entityId)
        targetArea.add(card, 0)

        return card
    }

    fun setCardAction(card: Card?) {
        card ?: return
        val deckStrategy = DeckStrategyActuator.deckStrategy
        deckStrategy ?: return

        val supplier: Supplier<CardAction>? = (CARD_ACTION_MAP[deckStrategy.pluginId] ?: let {
            CARD_ACTION_MAP[""]
        })?.let {
            it[card.cardId]
        }

        val cardAction = supplier?.get() ?: if (card.action === DEFAULT) CommonCardAction() else card.action
        cardAction.belongCard = card
        card.action = cardAction
    }

    private val objectMapper = ObjectMapper();

    private var cardWeightRawData:List<WeightCard>? = null

    fun getCardWeightCache(): List<WeightCard>? {
        return cardWeightRawData
    }

    fun reloadCardWeight(weightCard: List<WeightCard>? = null) {
        val list = weightCard ?: readWeightConfig()
        CARD_WEIGHT_TRIE.clear()
        list.forEach {
            CARD_WEIGHT_TRIE[it.cardId] = it.weight
        }
        cardWeightRawData = list.toList()
    }

    fun readWeightConfig(weightPath: Path = WEIGHT_CONFIG_PATH): List<WeightCard> {
        val file = weightPath.toFile()
        if (!file.exists()) return emptyList()
        try {
            return objectMapper.readValue(file, object : TypeReference<List<WeightCard>>() {
            })
        } catch (e: IOException) {
            log.error(e) { "反序列化权重文件异常" }
        }
        return emptyList()
    }

    fun saveWeightConfig(weightCard: List<WeightCard>, weightPath: Path = WEIGHT_CONFIG_PATH) {
        val file = weightPath.toFile()
        if (!file.exists() || file.isDirectory) {
            try {
                FileUtil.deleteFile(file)
                file.getParentFile().mkdirs()
                file.createNewFile()
            } catch (e: IOException) {
                log.error(e) { "权重文件创建异常,$file" }
                return
            }
        }
        try {
            FileChannel.open(
                weightPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).use { fileChannel ->
                val buffer = ByteBuffer.wrap(objectMapper.writeValueAsBytes(weightCard))
                fileChannel.write(buffer)
            }
        } catch (e: IOException) {
            log.error(e) { "权重文件保存异常,$file" }
        }
    }

}
