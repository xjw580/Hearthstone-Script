package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.enums.TimeOperateEnum
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:17
 */
class TimeRule {

    var id: String = ""

//    private val name: ObjectProperty<String> = SimpleObjectProperty<String>()

    private val workTime: ObjectProperty<WorkTime> = SimpleObjectProperty<WorkTime>(WorkTime())

    private val operate: ObjectProperty<Set<TimeOperateEnum>> =
        SimpleObjectProperty<Set<TimeOperateEnum>>(emptySet<TimeOperateEnum>())

    constructor(workTime: WorkTime?, operates: Set<TimeOperateEnum>?) {
        id = UUID.randomUUID().toString()
//        this.name.set(name)
        this.workTime.set(workTime)
        operates?.let { this.operate.set(it) }
    }


//    fun getName(): String {
//        return name.get()
//    }
//
//    fun nameProperty(): ObjectProperty<String> {
//        return name
//    }
//
//    fun setName(name: String) {
//        this.name.set(name)
//    }

    fun getWorkTime(): WorkTime {
        return workTime.get()
    }

    fun workTimeProperty(): ObjectProperty<WorkTime> {
        return workTime
    }

    fun setWorkTime(workTime: WorkTime) {
        this.workTime.set(workTime)
    }

    fun getOperate(): Set<TimeOperateEnum> {
        return operate.get()
    }

    fun operateProperty(): ObjectProperty<Set<TimeOperateEnum>> {
        return operate
    }

    fun setOperate(operates: Set<TimeOperateEnum>) {
        this.operate.set(operates)
    }
}
