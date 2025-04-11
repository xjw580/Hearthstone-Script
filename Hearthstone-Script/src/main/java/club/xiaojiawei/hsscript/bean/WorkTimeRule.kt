package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.enums.OperateEnum
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:17
 */
class WorkTimeRule {

    private val workTime: ObjectProperty<WorkTime> = SimpleObjectProperty<WorkTime>(WorkTime())

    private val operates: ObjectProperty<Set<OperateEnum>> =
        SimpleObjectProperty<Set<OperateEnum>>(emptySet<OperateEnum>())

    private val enable: BooleanProperty = SimpleBooleanProperty(false)

    constructor()

    constructor(workTime: WorkTime?, operates: Set<OperateEnum>?, enable: Boolean) {
        workTime?.let {
            this.workTime.set(it)
        }
        operates?.let {
            this.operates.set(it)
        }
        this.enable.set(enable)
    }

    fun getWorkTime(): WorkTime {
        return workTime.get()
    }

    fun workTimeProperty(): ObjectProperty<WorkTime> {
        return workTime
    }

    fun setWorkTime(workTime: WorkTime) {
        this.workTime.set(workTime)
    }

    fun getOperate(): Set<OperateEnum> {
        return operates.get()
    }

    fun operateProperty(): ObjectProperty<Set<OperateEnum>> {
        return operates
    }

    fun setOperate(operates: Set<OperateEnum>) {
        this.operates.set(operates)
    }

    fun isEnable(): Boolean {
        return enable.get()
    }

    fun enableProperty(): BooleanProperty {
        return enable
    }

    fun setEnable(isEnable: Boolean) {
        this.enable.set(isEnable)
    }
}
