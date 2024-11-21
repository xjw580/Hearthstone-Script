package club.xiaojiawei.hsscript.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.beans.property.SimpleDoubleProperty

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:14
 */
class WeightCard() {

    constructor(cardId: String, name: String, weight: Double, powerWeight: Double) : this() {
        this.cardId = cardId
        this.name = name
        this.weight = weight
        this.powerWeight = powerWeight
    }

    var cardId: String = ""
    var name: String = ""

    @JsonIgnore
    val weightProperty = SimpleDoubleProperty(0.0)

    @JsonIgnore
    val powerWeightProperty = SimpleDoubleProperty(0.0)

    var weight: Double
        get() = weightProperty.get()
        set(value) {
            weightProperty.set(value)
        }

    var powerWeight: Double
        get() = powerWeightProperty.get()
        set(value) {
            powerWeightProperty.set(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as WeightCard

        return cardId == other.cardId
    }

    override fun hashCode(): Int {
        return cardId.hashCode()
    }
}