package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.enums.CardActionEnum
import club.xiaojiawei.hsscript.enums.CardEffectTypeEnum
import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

/**
 * @author 肖嘉威
 * @date 2025/6/9 15:16
 */
class InfoCard : Cloneable {

    @JsonIgnore
    val cardIdProperty = SimpleStringProperty("")

    @JsonIgnore
    val nameProperty = SimpleStringProperty("")

    @JsonIgnore
    val effectTypeProperty = SimpleObjectProperty(CardEffectTypeEnum.UNKNOWN)

    /**
     * 卡牌id
     */
    var cardId: String
        get() = cardIdProperty.value
        set(value) {
            cardIdProperty.set(value)
        }

    /**
     * 名称
     */
    var name: String
        get() = nameProperty.value
        set(value) {
            nameProperty.set(value)
        }

    /**
     * 效果类型
     */
    var effectType: CardEffectTypeEnum
        get() = effectTypeProperty.value
        set(value) {
            effectTypeProperty.set(value)
        }

    /**
     * 行为
     */
    var actions: List<CardActionEnum> = emptyList()


    constructor(
        cardId: String,
        name: String,
        effectType: CardEffectTypeEnum = CardEffectTypeEnum.UNKNOWN,
        actions: List<CardActionEnum> = emptyList()
    ) {
        this.cardId = cardId
        this.name = name
        this.effectType = effectType
        this.actions = actions
    }

    constructor()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InfoCard

        return cardIdProperty.get() == other.cardIdProperty.get()
    }

    override fun hashCode(): Int {
        return cardIdProperty.get().hashCode()
    }

    public override fun clone(): InfoCard {
        return InfoCard(cardId, name, effectType, actions)
    }

}