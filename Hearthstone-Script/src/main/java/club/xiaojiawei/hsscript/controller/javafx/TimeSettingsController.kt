package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.Modal
import club.xiaojiawei.controls.Time
import club.xiaojiawei.controls.ico.EditIco
import club.xiaojiawei.hsscript.bean.TimeRule
import club.xiaojiawei.hsscript.bean.TimeRuleSet
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.enums.TimeOperateEnum
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import java.net.URL
import java.time.LocalTime
import java.util.*

private const val SELECTED_OPERATION_STYLE_CLASS = "label-ui-success"

/**
 * @author 肖嘉威
 * @date 2025/4/8 12:18
 */
class TimeSettingsController : Initializable {

//    @FXML
//    protected lateinit var noCol: TableColumn<TimeRule, Number?>

//    @FXML
//    protected lateinit var nameCol: TableColumn<TimeRule, String?>

    lateinit var nameSetCol: TableColumn<TimeRuleSet, String?>
    lateinit var noSetCol: TableColumn<TimeRuleSet, Number?>
    lateinit var timeRuleSetTable: TableView<TimeRuleSet>

    @FXML
    protected lateinit var selectedTimeRuleTable: TableView<TimeRule>

    @FXML
    protected lateinit var selectedTimeCol: TableColumn<TimeRule, WorkTime?>

    @FXML
    protected lateinit var selectedAfterOperationCol: TableColumn<TimeRule, Set<TimeOperateEnum>?>

    @FXML
    protected lateinit var allTimeRuleTable: TableView<TimeRule>

    @FXML
    protected lateinit var allTimeCol: TableColumn<TimeRule, WorkTime?>

    @FXML
    protected lateinit var allAfterOperationCol: TableColumn<TimeRule, Set<TimeOperateEnum>?>

    @FXML
    protected lateinit var rootPane: TabPane

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        allTimeRuleTable.isEditable = true
//        noCol.cellValueFactory = NumCallback()

//        nameCol.isEditable = true
//        nameCol.setCellValueFactory { cellData -> cellData.value.nameProperty() }
//        nameCol.setCellFactory { p ->
//            object : TextFieldTableCellUI<TimeRule?, String?>(object : StringConverter<String?>() {
//                override fun toString(`object`: String?): String? {
//                    return `object`
//                }
//
//                override fun fromString(string: String?): String? {
//                    return string
//                }
//            }) {
//                override fun styleClass(): Array<out String?>? {
//                    return arrayOf("text-field-ui")
//                }
//
//                override fun commitEdit(p0: String?) {
//                    super.commitEdit(p0)
//                    storeConfig()
//                }
//            }
//        }

        allTimeCol.setCellValueFactory { cellData -> cellData.value.workTimeProperty() }
        allTimeCol.setCellFactory { p ->
            object : TableCell<TimeRule, WorkTime?>() {
                override fun updateItem(item: WorkTime?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        graphic = null
                        return
                    }
                    graphic = buildTimePane(allTimeRuleTable.items[index])
                }
            }
        }

        allAfterOperationCol.setCellValueFactory { cellData -> cellData.value.operateProperty() }
        allAfterOperationCol.setCellFactory { p ->
            object : TableCell<TimeRule, Set<TimeOperateEnum>?>() {
                override fun updateItem(item: Set<TimeOperateEnum>?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        graphic = null
                        return
                    }
                    graphic = buildOperationPane(allTimeRuleTable.items[index])
                }
            }
        }

        allTimeRuleTable.items.addAll(
            TimeRule(
                WorkTime(LocalTime.now(), LocalTime.now().plusMinutes(30)),
                setOf(TimeOperateEnum.SLEEP_SYSTEM, TimeOperateEnum.LOCK_SCREEN, TimeOperateEnum.CLOSE_GAME)
            )
        )
    }


    private fun buildTimePane(item: TimeRule): Pane {
        return HBox(
            Time().apply {
                localTime = item.getWorkTime().parseStartTime()
                readOnlyTimeProperty().addListener { observable, oldValue, newValue ->
                    item.getWorkTime().startTime = WorkTime.pattern.format(newValue)
                    storeConfig()
                }
            },
            Text("-"),
            Time().apply {
                localTime = item.getWorkTime().parseEndTime()
                readOnlyTimeProperty().addListener { observable, oldValue, newValue ->
                    item.getWorkTime().endTime = WorkTime.pattern.format(newValue)
                    storeConfig()
                }
            },
        ).apply {
            padding = Insets(1.0)
            alignment = Pos.CENTER
            spacing = 3.0
        }
    }

    private fun buildOperationPane(item: TimeRule): Pane {
        return HBox().apply {
            children.addAll(item.getOperate().map {
                Label(it.value).apply {
                    styleClass.addAll("label-ui", "label-ui-small", SELECTED_OPERATION_STYLE_CLASS, "radius-ui")
                }
            }.toList())
            children.add(buildOperationEditBtn(item))
            alignment = Pos.CENTER
            spacing = 10.0
        }
    }

    private fun buildOperationEditBtn(item: TimeRule): Button {
        return Button().apply {
            graphic = EditIco("main-color")
            style =
                "-fx-border-radius:10;-fx-background-color: transparent;-fx-border-width: 1;-fx-border-color:gray;-fx-background-insets: 0"
            cursor = Cursor.HAND

            val operates = item.getOperate()
            val selectedTimeOperateEnums = mutableSetOf<TimeOperateEnum>()

            onAction = EventHandler<ActionEvent> {
                val content = FlowPane().apply {
                    val selectAllLabel = Label("全选")
                    val isSelectAll = {
                        if (selectedTimeOperateEnums.size == TimeOperateEnum.values().size) {
                            if (!selectAllLabel.styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                selectAllLabel.styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                            }
                        } else {
                            selectAllLabel.styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                        }
                    }
                    children.addAll(TimeOperateEnum.values().map { operation ->
                        Label(operation.value).apply label@{
                            styleClass.addAll("label-ui", "label-ui-small", "radius-ui")
                            if (operates.contains(operation)) {
                                selectedTimeOperateEnums.add(operation)
                                styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                            }
                            (this@label).onMouseClicked = EventHandler { e ->
                                if (styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                    selectedTimeOperateEnums.remove(operation)
                                    styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                                } else {
                                    selectedTimeOperateEnums.add(operation)
                                    styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                }
                                isSelectAll()
                            }
                        }
                    })
                    children.add(selectAllLabel.apply label@{
                        styleClass.addAll("label-ui", "label-ui-small", "radius-ui")
                        isSelectAll()
                        (this@label).onMouseClicked = EventHandler { e ->
                            if (styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                selectedTimeOperateEnums.clear()
                                for (node in children) {
                                    node.styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                                }
                            } else {
                                selectedTimeOperateEnums.addAll(TimeOperateEnum.values())
                                for (node in children) {
                                    if (!node.styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                        node.styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                    }
                                }
                            }
                        }
                    })
                    cursor = Cursor.HAND
                    hgap = 5.0
                    vgap = 5.0
                }
                Modal(rootPane, "修改", content, {
                    item.setOperate(selectedTimeOperateEnums)
                    storeConfig()
                }, {}).apply {
                    isMaskClosable = true
                }.show()
            }
        }
    }

    private fun storeConfig() {
        println("storeConfig")
        for (rule in allTimeRuleTable.items) {
            println(rule.getWorkTime().startTime + "-" + rule.getWorkTime().endTime)
            println(rule.getOperate())
        }
    }

}
