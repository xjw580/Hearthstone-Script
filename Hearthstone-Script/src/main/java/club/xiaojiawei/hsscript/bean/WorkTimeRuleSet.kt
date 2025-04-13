package club.xiaojiawei.hsscript.bean

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:17
 */
class WorkTimeRuleSet : Cloneable {
    var id: String = ""

    private val name: ObjectProperty<String> = SimpleObjectProperty<String>("")

    private val workTimeRules: ObjectProperty<List<WorkTimeRule>> =
        SimpleObjectProperty<List<WorkTimeRule>>(emptyList<WorkTimeRule>())

    constructor() {}

    constructor(
        name: String,
        workTimeRules: List<WorkTimeRule> = emptyList<WorkTimeRule>(),
        id: String = UUID.randomUUID().toString(),
    ) {
        this.id = id
        this.name.set(name)
        this.workTimeRules.set(workTimeRules)
    }

    fun reGenerateId() {
        id = UUID.randomUUID().toString()
    }

    fun getName(): String = name.get()

    fun nameProperty(): ObjectProperty<String> = name

    fun setName(name: String) {
        this.name.set(name)
    }

    fun getTimeRules(): List<WorkTimeRule> = workTimeRules.get()

    fun timeRulesProperty(): ObjectProperty<String> = name

    fun setTimeRules(workTimeRules: List<WorkTimeRule>) {
        this.workTimeRules.set(workTimeRules)
    }

    override fun equals(o: Any?): Boolean {
        if (o == null || javaClass != o.javaClass) return false
        val workTime = o as WorkTimeRuleSet
        return id == workTime.id
    }

    override fun hashCode(): Int = Objects.hashCode(id)

    public override fun clone(): WorkTimeRuleSet {
        val clone = WorkTimeRuleSet()
        clone.id = this.id
        clone.name.set(this.name.get())
        clone.workTimeRules.set(
            this.workTimeRules
                .get()
                .map { it.clone() }
                .toList(),
        )
        return clone
    }
}
