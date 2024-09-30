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
import club.xiaojiawei.status.War
import club.xiaojiawei.strategy.DeckStrategyActuator
import javafx.beans.value.ObservableValue
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
            BaseCardMapper.INSTANCE.update(extraEntity.extraCard.card, card)
            EntityMapper.INSTANCE.update(extraEntity, card)
        }
    }

    fun exchangeAreaOfCard(extraEntity: ExtraEntity): Card? {
        val sourceArea = ScriptStaticData.CARD_AREA_MAP[extraEntity.entityId] ?: let {
            War.getPlayer(extraEntity.playerId).getArea(extraEntity.zone)
        } ?: return null
        val targetArea = War.getPlayer(extraEntity.playerId).getArea(extraEntity.extraCard.zone) ?: return null

        val card = sourceArea.removeByEntityId(extraEntity.entityId)
        targetArea.add(card, extraEntity.extraCard.zonePos)

        return card
    }

    fun exchangeAreaOfCard(tagChangeEntity: TagChangeEntity): Card? {
        val sourceArea = ScriptStaticData.CARD_AREA_MAP[tagChangeEntity.entityId] ?: return null
        val targetArea =
            War.getPlayer(tagChangeEntity.playerId).getArea(ZoneEnum.valueOf(tagChangeEntity.value)) ?: return null

        val card = sourceArea.removeByEntityId(tagChangeEntity.entityId)
        targetArea.add(card, 0)

        return card
    }

    fun setCardAction(card: Card?) {
        card?:return
        val deckStrategy = DeckStrategyActuator.deckStrategy
        deckStrategy?:return

        val supplier: Supplier<CardAction>? = (CARD_ACTION_MAP[deckStrategy.pluginId] ?: let {
            CARD_ACTION_MAP[""]
        })?.let {
            it[card.cardId]
        }

        val cardAction = supplier?.get() ?: if (card.action === DEFAULT) CommonCardAction() else card.action
        cardAction.belongCard = card
        card.action = cardAction
    }

}
