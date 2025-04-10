package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.Time
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.bean.WorkTimeRule
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.WorkTimeStatus
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.CheckBox
import javafx.scene.layout.HBox

/**
 * @author 肖嘉威
 * @date 2025/4/10 13:03
 */
class WorkTimeItem(val workTimeRule: WorkTimeRule, val changeId: String) : HBox() {

    @FXML
    protected lateinit var startTime: Time

    @FXML
    protected lateinit var endTime: Time

    @FXML
    protected lateinit var enableCheckBox: CheckBox

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/WorkTimeItem.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        afterLoaded()
    }

    private fun afterLoaded() {
        startTime.time = workTimeRule.getWorkTime().startTime
        endTime.time = workTimeRule.getWorkTime().endTime
        enableCheckBox.isSelected = workTimeRule.isEnable()

        startTime.timeProperty().addListener { observable, oldValue, newValue ->
            newValue ?: return@addListener
            workTimeRule.setWorkTime(WorkTime(WorkTime.pattern.format(newValue), workTimeRule.getWorkTime().endTime))
//            workTimeRule.getWorkTime().startTime = WorkTime.pattern.format(newValue)
            WorkTimeStatus.storeWorkTimeRuleSet(changeId = changeId)
            WorkListener.checkWork()
        }

        endTime.timeProperty().addListener { observable, oldValue, newValue ->
            newValue ?: return@addListener
//            workTimeRule.getWorkTime().endTime = WorkTime.pattern.format(newValue)
            workTimeRule.setWorkTime(WorkTime(workTimeRule.getWorkTime().startTime, WorkTime.pattern.format(newValue)))
            WorkTimeStatus.storeWorkTimeRuleSet(changeId = changeId)
            WorkListener.checkWork()
        }

        enableCheckBox.selectedProperty().addListener { observable, oldValue, newValue ->
            workTimeRule.setEnable(newValue)
            WorkTimeStatus.storeWorkTimeRuleSet(changeId = changeId)
            WorkListener.checkWork()
        }
    }
}