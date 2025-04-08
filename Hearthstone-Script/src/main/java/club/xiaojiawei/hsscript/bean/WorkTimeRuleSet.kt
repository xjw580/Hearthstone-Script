package club.xiaojiawei.hsscript.bean

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:17
 */
class WorkTimeRuleSet {

    var id: String = ""

    private val name: ObjectProperty<String> = SimpleObjectProperty<String>()

    private val workTimeRules: ObjectProperty<List<WorkTimeRule>> = SimpleObjectProperty<List<WorkTimeRule>>(emptyList<WorkTimeRule>())

    constructor(){}

    constructor(name: String, workTimeRules: List<WorkTimeRule> = emptyList<WorkTimeRule>()) {
        id = UUID.randomUUID().toString()
        this.name.set(name)
        this.workTimeRules.set(workTimeRules)
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

    fun getTimeRules(): List<WorkTimeRule> {
        return workTimeRules.get()
    }

    fun timeRulesProperty(): ObjectProperty<String> {
        return name
    }

    fun setTimeRules(workTimeRules: List<WorkTimeRule>) {
        this.workTimeRules.set(workTimeRules)
    }
//{"id":"92778c9e-8dc3-48cb-ac32-e6743579f1eb","name":"预设","timeRules":[{"operate":["SLEEP_SYSTEM","LOCK_SCREEN","CLOSE_GAME","CLOSE_PLATFORM"],"workTime":{"enabled":true,"endTime":"08:00","startTime":"00:00"}},{"operate":{"$ref":"$.timeRules.operate"},"workTime":{"enabled":true,"endTime":"14:00","startTime":"12:00"}}]}
}
