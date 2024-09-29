package club.xiaojiawei.utils

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CommonCardAction
import club.xiaojiawei.bean.CommonCardAction.Companion.DEFAULT
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.bean.log.ExtraEntity
import club.xiaojiawei.bean.log.TagChangeEntity
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.enums.ZoneEnum
import club.xiaojiawei.mapper.BaseCardMapper
import club.xiaojiawei.mapper.EntityMapper
import club.xiaojiawei.status.CardActionManager.CARD_ACTION_MAP
import club.xiaojiawei.status.War.getPlayer
import club.xiaojiawei.strategy.DeckStrategyActuator.deckStrategy
import javafx.beans.value.ObservableValue
import org.springframework.stereotype.Component
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/6 21:07
 */
object CardUtil {

    fun addAreaListener(card: Card?) {
        card?.let {
            card.areaProperty.addListener { _: ObservableValue<out Area?>?, _: Area?, newArea: Area? ->
                ScriptStaticData.CARD_AREA_MAP.remove(card.entityId)
                ScriptStaticData.CARD_AREA_MAP[card.entityId] = newArea
            }
        }
    }

    fun updateCardByExtraEntity(extraEntity: ExtraEntity, card: Card?) {
        card?.let {
            BaseCardMapper.INSTANCE.update(extraEntity.getExtraCard().getCard(), card)
            EntityMapper.INSTANCE.update(extraEntity, card)
        }
    }

    fun exchangeAreaOfCard(extraEntity: ExtraEntity): Card? {
        val sourceArea = ScriptStaticData.CARD_AREA_MAP[extraEntity.entityId] ?: let {
            getPlayer(extraEntity.playerId).getArea(extraEntity.zone)
        }
        val targetArea = getPlayer(extraEntity.playerId).getArea(extraEntity.getExtraCard().zone)

        val card = sourceArea.removeByEntityId(extraEntity.entityId)

        targetArea.add(card, extraEntity.getExtraCard().zonePos)

        return card
    }

    fun exchangeAreaOfCard(tagChangeEntity: TagChangeEntity) {
        val sourceArea = ScriptStaticData.CARD_AREA_MAP[tagChangeEntity.entityId]
        val targetArea = getPlayer(tagChangeEntity.playerId)!!.getArea(ZoneEnum.valueOf(tagChangeEntity.value))
        targetArea.add(sourceArea!!.removeByEntityId(tagChangeEntity.entityId), 0)
    }

    fun setCardAction(card: Card?) {
        if (card == null) {
            return
        }
        var supplier: Supplier<CardAction>? = null
        val deckStrategy = deckStrategy
        var map: Map<String?, Supplier<CardAction>?>? = CARD_ACTION_MAP[deckStrategy!!.pluginId]
        if (map == null) {
            map = CARD_ACTION_MAP[""]
        }
        if (map != null) {
            supplier = map[card.cardId]
        }
        val cardAction = supplier?.get() ?: if (card.action === DEFAULT) CommonCardAction() else card.action
        cardAction.belongCard = card
        card.action = cardAction
    }
}
