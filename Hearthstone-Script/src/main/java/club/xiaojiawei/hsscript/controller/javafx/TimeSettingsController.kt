package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.config.log
import club.xiaojiawei.controls.Modal
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ProgressModal
import club.xiaojiawei.controls.Time
import club.xiaojiawei.controls.ico.EditIco
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.bean.WorkTimeRule
import club.xiaojiawei.hsscript.bean.WorkTimeRuleSet
import club.xiaojiawei.hsscript.bean.tableview.NumCallback
import club.xiaojiawei.hsscript.enums.TimeOperateEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.status.WorkTimeStatus
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import club.xiaojiawei.util.isFalse
import javafx.beans.property.DoubleProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.util.StringConverter
import java.net.URL
import java.util.*

private const val SELECTED_OPERATION_STYLE_CLASS = "label-ui-success"

/**
 * @author 肖嘉威
 * @date 2025/4/8 12:18
 */
class TimeSettingsController : Initializable, StageHook {

    @FXML
    protected lateinit var accordion: Accordion

    @FXML
    protected lateinit var applyRulePane: TitledPane

    @FXML
    protected lateinit var setRulePane: TitledPane

    @FXML
    protected lateinit var sunComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var satComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var friComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var thuComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var wedComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var tueComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var monComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var everyDayComboBox: ComboBox<WorkTimeRuleSet?>

    @FXML
    protected lateinit var progressModal: ProgressModal

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var noSetCol: TableColumn<WorkTimeRuleSet, Number?>

    @FXML
    protected lateinit var nameSetCol: TableColumn<WorkTimeRuleSet, String?>

    @FXML
    protected lateinit var workTimeRuleSetTable: TableView<WorkTimeRuleSet>

    @FXML
    protected lateinit var selectedWorkTimeRuleTable: TableView<WorkTimeRule>

    @FXML
    protected lateinit var selectedTimeCol: TableColumn<WorkTimeRule, WorkTime?>

    @FXML
    protected lateinit var selectedAfterOperationCol: TableColumn<WorkTimeRule, Set<TimeOperateEnum>?>

    @FXML
    protected lateinit var selectedEnableCol: TableColumn<WorkTimeRule, Boolean>

    @FXML
    protected lateinit var rootPane: Pane

    private var progress: DoubleProperty? = null

    private var workTimeRuleSet: MutableList<WorkTimeRuleSet>? = null

    private val dateComboBoxList = mutableListOf<ComboBox<WorkTimeRuleSet?>>()

    private var isInit = false

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        progress = progressModal.show()
        WindowEnum.TIME_SETTINGS.cache.isFalse {
            log.warn { WindowEnum.TIME_SETTINGS.name + "窗口没有缓存，将会导致内存泄漏" }
        }
    }

    override fun onShowing() {
        super.onShowing()
        if (isInit) return
        isInit = true
        initTimeRuleSetTable()
        initSelectedTimeRuleTable()
        initApplyRulePane()
        var firstExpandApplyRulePane = false
        accordion.expandedPaneProperty().addListener { observable, oldValue, newValue ->
            if (newValue === applyRulePane && !firstExpandApplyRulePane) {
                firstExpandApplyRulePane = true
                reloadApplyRulePane()
                loadWorkTimeSetting()
            }
        }

        dateComboBoxList.addAll(
            listOf(
                everyDayComboBox.apply {
                    valueProperty().addListener { observable, oldValue, newValue ->
                        newValue?.let {
                            isSetAllDate = true
                            for (i in 1 until dateComboBoxList.size) {
                                dateComboBoxList[i].value = it
                            }
                            isSetAllDate = false
                        }
                    }
                },
                monComboBox.apply { checkEveryDate(this) },
                tueComboBox.apply { checkEveryDate(this) },
                wedComboBox.apply { checkEveryDate(this) },
                thuComboBox.apply { checkEveryDate(this) },
                friComboBox.apply { checkEveryDate(this) },
                satComboBox.apply { checkEveryDate(this) },
                sunComboBox.apply { checkEveryDate(this) },
            )
        )
        go {
            loadWorkTimeRuleSet()
            runUI {
                if (workTimeRuleSetTable.items.isNotEmpty()) {
                    workTimeRuleSetTable.selectionModel.selectFirst()
                }
                progressModal.hide(progress)
            }
        }
    }

    private var isSetAllDate = false

    private fun checkEveryDate(dateComboBox: ComboBox<WorkTimeRuleSet?>) {
        dateComboBox.valueProperty().addListener { _, _, newValue ->
            if (isSetAllDate) return@addListener
            val ruleTypeSet = mutableSetOf<WorkTimeRuleSet?>()
            for (i in 1 until dateComboBoxList.size) {
                ruleTypeSet.add(dateComboBoxList[i].value)
            }
            if (ruleTypeSet.size > 1) {
                everyDayComboBox.value = null
            } else if (ruleTypeSet.size == 1) {
                everyDayComboBox.value = ruleTypeSet.first()
            }
        }
    }

    private fun initApplyRulePane() {
        val converter = object : StringConverter<WorkTimeRuleSet?>() {
            override fun toString(p0: WorkTimeRuleSet?): String? {
                return p0?.getName()
            }

            override fun fromString(p0: String?): WorkTimeRuleSet? {
                return null
            }

        }
        everyDayComboBox.converter = converter
        monComboBox.converter = converter
        tueComboBox.converter = converter
        wedComboBox.converter = converter
        thuComboBox.converter = converter
        friComboBox.converter = converter
        satComboBox.converter = converter
        sunComboBox.converter = converter
    }

    private fun initTimeRuleSetTable() {
        workTimeRuleSetTable.isEditable = true
        workTimeRuleSetTable.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            selectedWorkTimeRuleTable.items.setAll(newValue.getTimeRules())
        }
        noSetCol.cellValueFactory = NumCallback()
        nameSetCol.isEditable = true
        nameSetCol.setCellValueFactory { cellData -> cellData.value.nameProperty() }
        nameSetCol.setCellFactory { p ->
            object : TextFieldTableCellUI<WorkTimeRuleSet?, String?>(object : StringConverter<String?>() {
                override fun toString(`object`: String?): String? {
                    return `object`
                }

                override fun fromString(string: String?): String? {
                    return string
                }
            }) {
                override fun cancelEdit() {
                    super.commitEdit(node.text)
                }

                override fun commitEdit(p0: String?) {
                    super.commitEdit(p0)
                }
            }

        }
    }

    private fun reloadApplyRulePane() {
        val rule = workTimeRuleSet?.toMutableList()?.apply { addFirst(WorkTimeRuleSet.NONE) }
        for (box in dateComboBoxList) {
            rule?.let {
                val selectedItem = box.selectionModel.selectedItem
                box.items.setAll(it)
                box.value = selectedItem
            } ?: let {
                box.items.clear()
            }
        }
    }

    private fun loadWorkTimeRuleSet() {
        val workTimeRuleSet = WorkTimeStatus.readOnlyWorkTimeRuleSet().toMutableList()
        this.workTimeRuleSet = workTimeRuleSet
        workTimeRuleSet.let {
            workTimeRuleSetTable.items.addAll(it)
        }
    }

    private fun loadWorkTimeSetting() {
        val workTimeSetting = WorkTimeStatus.readOnlyWorkTimeSetting().toTypedArray()
        for ((i, id) in workTimeSetting.withIndex()) {
            if (id == WorkTimeRuleSet.NONE_WORK_TIME_RULE_SET_ID) {
                dateComboBoxList[i].value = WorkTimeRuleSet.NONE
            } else {
                dateComboBoxList[i].value = workTimeRuleSet?.find { it.id == id }
            }
        }
    }

    private fun initSelectedTimeRuleTable() {
        selectedWorkTimeRuleTable.isEditable = true

        selectedTimeCol.setCellValueFactory { cellData -> cellData.value.workTimeProperty() }
        selectedTimeCol.setCellFactory { p ->
            ColTableCell { index -> buildTimePane(selectedWorkTimeRuleTable.items[index]) }
        }

        selectedAfterOperationCol.setCellValueFactory { cellData -> cellData.value.operateProperty() }
        selectedAfterOperationCol.setCellFactory { p ->
            ColTableCell { index -> buildOperationPane(selectedWorkTimeRuleTable.items[index]) }
        }

        selectedEnableCol.setCellValueFactory { cellData -> cellData.value.enableProperty() }
        selectedEnableCol.setCellFactory { p ->
            object : CheckBoxTableCell<WorkTimeRule, Boolean>() {
                override fun updateItem(item: Boolean?, empty: Boolean) {
                    super.updateItem(item, empty)
                    graphic?.let {
                        val styleClass = "check-box-ui"
                        if (!it.styleClass.contains(styleClass)) {
                            it.styleClass.add(styleClass)
                            it.styleClass.add("check-box-ui-main")
                        }
                    }
                }
            }
        }
    }

    private class ColTableCell<T, S>(var buildGraphic: (Int) -> Node) : TableCell<T, S>() {

        override fun updateItem(item: S?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item == null || empty) {
                graphic = null
                return
            }
            graphic = this.buildGraphic(index)
        }

    }

    private var timePaneMap = mutableMapOf<WorkTimeRule, Pane>()

    private fun buildTimePane(item: WorkTimeRule): Pane {
        timePaneMap[item]?.let {
            return it
        }
        val startTime = Time()
        val endTime = Time()
        val pane = HBox(
            startTime.apply {
                localTime = item.getWorkTime().parseStartTime()
                readOnlyTimeProperty().addListener { observable, oldValue, newValue ->
                    newValue ?: return@addListener
                    item.getWorkTime().startTime = WorkTime.pattern.format(newValue)
                }
            },
            Text("-"),
            endTime.apply {
                localTime = item.getWorkTime().parseEndTime()
                readOnlyTimeProperty().addListener { observable, oldValue, newValue ->
                    newValue ?: return@addListener
                    item.getWorkTime().endTime = WorkTime.pattern.format(newValue)
                }
            },
        ).apply {
            padding = Insets(1.0)
            alignment = Pos.CENTER
            spacing = 3.0
//            本窗口必须缓存，不然此项监听将导致内存泄漏
            item.workTimeProperty().addListener { observable, oldValue, newValue ->
                startTime.time = newValue.startTime
                endTime.time = newValue.endTime
            }
        }
        timePaneMap[item] = pane
        return pane
    }

    private fun buildOperationPane(item: WorkTimeRule): Pane {
        val pane = HBox().apply {
            children.addAll(item.getOperate().map {
                Label(it.value).apply {
                    styleClass.addAll("label-ui", "label-ui-small", SELECTED_OPERATION_STYLE_CLASS, "radius-ui")
                }
            }.toList())
            children.add(buildOperationEditBtn(item))
            alignment = Pos.CENTER
            spacing = 5.0
        }
        return pane
    }

    private fun buildOperationEditBtn(item: WorkTimeRule): Button {
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
                    val isSelectAll: () -> Unit = {
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
                }, {}).apply {
                    isMaskClosable = true
                }.show()
            }
        }
    }

    private fun saveSetRule() {
        WorkTimeStatus.storeWorkTimeRuleSet(workTimeRuleSetTable.items)
        notificationManager.showSuccess("定义规则保存成功", 2)
        workTimeRuleSet = workTimeRuleSetTable.items.toMutableList()
        reloadApplyRulePane()
    }

    private fun saveApplyRule() {
        val workTimeSetting =
            dateComboBoxList.map { it.value?.id ?: WorkTimeRuleSet.NONE_WORK_TIME_RULE_SET_ID }.toMutableList().apply {
                removeFirst()
            }
        WorkTimeStatus.storeWorkTimeSetting(workTimeSetting)
        notificationManager.showSuccess("应用规则保存成功", 2)
    }

    @FXML
    protected fun save() {
        if (setRulePane.isExpanded) {
            saveSetRule()
        } else if (applyRulePane.isExpanded) {
            saveApplyRule()
        } else {
            saveSetRule()
            saveApplyRule()
        }
    }

    @FXML
    protected fun addRulerSet(actionEvent: ActionEvent) {
        workTimeRuleSetTable.items.add(
            WorkTimeRuleSet("预设${workTimeRuleSetTable.items.size + 1}")
        )
    }

    @FXML
    protected fun delRulerSet(actionEvent: ActionEvent) {
        val index = workTimeRuleSetTable.selectionModel.selectedIndex
        if (index == -1) return
        workTimeRuleSetTable.items.removeAt(index)
    }

    @FXML
    protected fun addRuler(actionEvent: ActionEvent) {
        val workTimeRuleSet = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        val workTimeRule = WorkTimeRule().apply {
            setWorkTime(WorkTime("00:00", "00:00"))
            setEnable(true)
        }
        workTimeRuleSet.setTimeRules(workTimeRuleSet.getTimeRules() + workTimeRule)
        selectedWorkTimeRuleTable.items.add(workTimeRule)
    }

    @FXML
    protected fun delRuler(actionEvent: ActionEvent) {
        val workTimeRule = selectedWorkTimeRuleTable.selectionModel.selectedItem ?: return
        val workTimeRuleSet = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        workTimeRuleSet.setTimeRules(workTimeRuleSet.getTimeRules().toMutableList().apply {
            remove(workTimeRule)
        })
        selectedWorkTimeRuleTable.items.remove(workTimeRule)
    }

}
