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

    private val name: ObjectProperty<String> = SimpleObjectProperty<String>("")

    private val workTimeRules: ObjectProperty<List<WorkTimeRule>> =
        SimpleObjectProperty<List<WorkTimeRule>>(emptyList<WorkTimeRule>())

    constructor() {}

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

    override fun equals(o: Any?): Boolean {
        if (o == null || javaClass != o.javaClass) return false
        val workTime = o as WorkTimeRuleSet
        return id == workTime.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }

    companion object {
        const val NONE_WORK_TIME_RULE_SET_ID = "none"

        val NONE = WorkTimeRuleSet().apply {
            id = NONE_WORK_TIME_RULE_SET_ID
            setName("空")
        }

    }

}
