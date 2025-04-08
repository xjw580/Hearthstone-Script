package club.xiaojiawei.hsscript.bean

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:17
 */
class TimeRuleSet {

    var id: String = ""

    private val name: ObjectProperty<String> = SimpleObjectProperty<String>()

    private val timeRules: ObjectProperty<Set<TimeRule>> = SimpleObjectProperty<Set<TimeRule>>(emptySet<TimeRule>())

    constructor(name: String, workTime: WorkTime?, timeRules: Set<TimeRule> = emptySet<TimeRule>()) {
        id = UUID.randomUUID().toString()
        this.name.set(name)
        this.timeRules.set(timeRules)
    }

    fun getName(): String {
        return name.get()
    }

    fun nameProperty(): ObjectProperty<String> {
        return name
    }

    fun setName(name: String) {
        this.name.set(name)
    }

    fun getTimeRules(): Set<TimeRule> {
        return timeRules.get()
    }

    fun timeRulesProperty(): ObjectProperty<String> {
        return name
    }

    fun setTimeRules(timeRules: Set<TimeRule>) {
        this.timeRules.set(timeRules)
    }

}
