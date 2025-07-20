package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.Modal
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ProgressModal
import club.xiaojiawei.controls.Time
import club.xiaojiawei.controls.ico.EditIco
import club.xiaojiawei.controls.ico.HelpIco
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.bean.WorkTimeRule
import club.xiaojiawei.hsscript.bean.WorkTimeRuleSet
import club.xiaojiawei.hsscript.bean.tableview.NumCallback
import club.xiaojiawei.hsscript.bean.tableview.TableDragCallback
import club.xiaojiawei.hsscript.enums.OperateEnum
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.status.WorkTimeStatus
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import javafx.beans.property.DoubleProperty
import javafx.collections.ObservableList
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
import javafx.util.Duration
import javafx.util.StringConverter
import java.net.URL
import java.util.*

private const val SELECTED_OPERATION_STYLE_CLASS = "label-ui-success"

/**
 * @author 肖嘉威
 * @date 2025/4/8 12:18
 */
class TimeSettingsController :
    Initializable,
    StageHook {
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
    protected lateinit var selectedAfterOperationCol: TableColumn<WorkTimeRule, Set<OperateEnum>?>

    @FXML
    protected lateinit var selectedEnableCol: TableColumn<WorkTimeRule, Boolean>

    @FXML
    protected lateinit var rootPane: Pane

    private var progress: DoubleProperty? = null

    private lateinit var workTimeRuleSet: ObservableList<WorkTimeRuleSet>

    private val dateComboBoxList = mutableListOf<ComboBox<WorkTimeRuleSet?>>()

    private var isInit = false

    override fun initialize(
        p0: URL?,
        p1: ResourceBundle?,
    ) {
        progress = progressModal.show()
        workTimeRuleSet = workTimeRuleSetTable.items
    }

    fun reloadData() {
        loadWorkTimeRuleSet()
        updateDateComboBoxItems()
        loadWorkTimeSetting()
    }

    override fun onShowing() {
        super.onShowing()
        if (isInit) {
            reloadData()
            return
        }
        workTimeRuleSetTable.rowFactory = TableDragCallback<WorkTimeRuleSet, WorkTimeRuleSet>()
        selectedWorkTimeRuleTable.rowFactory =
            object : TableDragCallback<WorkTimeRule, WorkTimeRule>() {
                override fun dragged(srcIndex: Int, destIndex: Int) {
                    val workTimeRuleSet = workTimeRuleSetTable.selectionModel.selectedItem ?: return
                    workTimeRuleSet.setTimeRules(selectedWorkTimeRuleTable.items)
                    workTimeRuleSet.getTimeRules()
                }
            }
        isInit = true
        initTimeRuleSetTable()
        initSelectedTimeRuleTable()
        initApplyRulePane()

        dateComboBoxList.addAll(
            listOf(
                everyDayComboBox.apply {
                    items = workTimeRuleSetTable.items
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
            ),
        )
        go {
            reloadData()
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
        dateComboBox.items = workTimeRuleSetTable.items
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
        val converter =
            object : StringConverter<WorkTimeRuleSet?>() {
                override fun toString(p0: WorkTimeRuleSet?): String? = p0?.getName()

                override fun fromString(p0: String?): WorkTimeRuleSet? = null
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
            if (newValue == null) {
                selectedWorkTimeRuleTable.items.clear()
                return@addListener
            }
            selectedWorkTimeRuleTable.items.setAll(newValue.getTimeRules())
        }
        noSetCol.cellValueFactory = NumCallback()
        nameSetCol.isEditable = true
        nameSetCol.setCellValueFactory { cellData -> cellData.value.nameProperty() }
        nameSetCol.setCellFactory { p ->
            object : TextFieldTableCellUI<WorkTimeRuleSet?, String?>(
                object : StringConverter<String?>() {
                    override fun toString(`object`: String?): String? = `object`

                    override fun fromString(string: String?): String? = string
                },
            ) {
                override fun cancelEdit() {
                    if (workTimeRuleSetTable.items[index].id.isEmpty()) {
                        super.cancelEdit()
                        notificationManager.showInfo("不允许修改该规则", 2)
                        return
                    }
                    super.commitEdit(node.text)
                    updateDateComboBoxItems()
                }

                override fun commitEdit(p0: String?) {
                    if (workTimeRuleSetTable.items[index].id.isEmpty()) {
                        super.cancelEdit()
                        notificationManager.showInfo("不允许修改该规则", 2)
                        return
                    }
                    super.commitEdit(p0)
                    updateDateComboBoxItems()
                }
            }
        }
    }

    private fun updateDateComboBoxItems() {
        for (box in dateComboBoxList) {
            val selectedItem = box.selectionModel.selectedItem
            box.selectionModel.select(null)
            box.selectionModel.select(selectedItem)
        }
    }

    private fun loadWorkTimeRuleSet() {
        val workTimeRuleSet = WorkTimeStatus.readOnlyWorkTimeRuleSet().get()
        val selectedItem = workTimeRuleSetTable.selectionModel.selectedItem
        this.workTimeRuleSet.setAll(workTimeRuleSet)
        workTimeRuleSetTable.selectionModel.select(selectedItem)
        workTimeRuleSetTable.refresh()
    }

    private fun loadWorkTimeSetting() {
        val workTimeSetting = ConfigExUtil.getWorkTimeSetting()
        for ((i, id) in workTimeSetting.withIndex()) {
            dateComboBoxList[i + 1].selectionModel.select(workTimeRuleSet.find { it.id == id })
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
                override fun updateItem(
                    item: Boolean?,
                    empty: Boolean,
                ) {
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

    private class ColTableCell<T, S>(
        var buildGraphic: (Int) -> Node,
    ) : TableCell<T, S>() {
        override fun updateItem(
            item: S?,
            empty: Boolean,
        ) {
            super.updateItem(item, empty)
            if (item == null || empty) {
                graphic = null
                return
            }
            graphic = this.buildGraphic(index)
        }
    }

    private var timePaneMap = mutableMapOf<WorkTimeRule, HBox>()

    private fun buildTimePane(item: WorkTimeRule): HBox {
        timePaneMap[item]?.let {
            for ((index, node) in it.children.withIndex()) {
                if (node is Time) {
                    if (index == 0) {
                        node.localTime
                    }
                }
            }
            return it
        }
        val startTime = Time()
        val endTime = Time()
        val pane =
            HBox(
                startTime.apply {
                    localTime = item.getWorkTime().parseStartTime()
                    readOnlyTimeProperty().addListener { _, _, newValue ->
                        newValue ?: return@addListener
                        item.getWorkTime().startTime = WorkTime.pattern.format(newValue)
                        if (newValue > endTime.localTime) {
                            endTime.localTime = newValue
                        }
                    }
                },
                Text("-"),
                endTime.apply {
                    localTime = item.getWorkTime().parseEndTime()
                    readOnlyTimeProperty().addListener { _, _, newValue ->
                        newValue ?: return@addListener
                        item.getWorkTime().endTime = WorkTime.pattern.format(newValue)
                        if (newValue < startTime.localTime) {
                            startTime.localTime = newValue
                        }
                    }
                },
            ).apply {
                padding = Insets(1.0)
                alignment = Pos.CENTER
                spacing = 3.0
            }
        timePaneMap[item] = pane
        return pane
    }

    private fun buildOperationPane(item: WorkTimeRule): Pane {
        val pane =
            HBox().apply {
                children.addAll(
                    item
                        .getOperate()
                        .toMutableList()
                        .apply { sortBy { it.order } }
                        .map {
                            Label(it.value).apply {
                                styleClass.addAll(
                                    "label-ui",
                                    "label-ui-small",
                                    SELECTED_OPERATION_STYLE_CLASS,
                                    "radius-ui",
                                )
                                if (it === OperateEnum.SLEEP_SYSTEM || it === OperateEnum.LOCK_SCREEN) {
                                    contentDisplay = ContentDisplay.RIGHT
                                    graphic =
                                        Label().apply graphic@{
                                            this@graphic.graphic = HelpIco()
                                            this@graphic.tooltip =
                                                Tooltip(
                                                    if (it ===
                                                        OperateEnum.SLEEP_SYSTEM
                                                    ) {
                                                        "在Windows设置中关闭唤醒电脑需要重新登录选项"
                                                    } else {
                                                        "锁屏后无法自动解锁"
                                                    },
                                                ).apply tooltip@{
                                                    showDuration = Duration.seconds(60.0)
                                                }
                                        }
                                }
                            }
                        }.toList(),
                )
                children.add(buildOperationEditBtn(item))
                alignment = Pos.CENTER
                spacing = 5.0
            }
        return pane
    }

    private fun buildOperationEditBtn(item: WorkTimeRule): Button =
        Button().apply {
            graphic = EditIco("main-color")
            style =
                "-fx-border-radius:10;-fx-background-color: transparent;-fx-border-width: 1;-fx-border-color:gray;-fx-background-insets: 0"
            cursor = Cursor.HAND

            val operates = item.getOperate()
            val selectedOperateEnums = mutableSetOf<OperateEnum>()

            onAction =
                EventHandler<ActionEvent> {
                    val content =
                        FlowPane().apply {
                            val selectAllLabel = Label("全选")
                            val isSelectAll: () -> Unit = {
                                if (selectedOperateEnums.size == OperateEnum.values().size) {
                                    if (!selectAllLabel.styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                        selectAllLabel.styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                    }
                                } else {
                                    selectAllLabel.styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                                }
                            }
                            children.addAll(
                                OperateEnum
                                    .values()
                                    .toMutableList()
                                    .apply { sortBy { it.order } }
                                    .map { operation ->
                                        Label(operation.value).apply label@{
                                            styleClass.addAll("label-ui", "label-ui-small", "radius-ui")
                                            if (operates.contains(operation)) {
                                                selectedOperateEnums.add(operation)
                                                styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                            }
                                            (this@label).onMouseClicked =
                                                EventHandler { e ->
                                                    if (styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                                        selectedOperateEnums.remove(operation)
                                                        styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                                                    } else {
                                                        selectedOperateEnums.add(operation)
                                                        styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                                    }
                                                    isSelectAll()
                                                }
                                        }
                                    },
                            )
                            children.add(
                                selectAllLabel.apply label@{
                                    styleClass.addAll("label-ui", "label-ui-small", "radius-ui")
                                    isSelectAll()
                                    (this@label).onMouseClicked =
                                        EventHandler { e ->
                                            if (styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                                selectedOperateEnums.clear()
                                                for (node in children) {
                                                    node.styleClass.remove(SELECTED_OPERATION_STYLE_CLASS)
                                                }
                                            } else {
                                                selectedOperateEnums.addAll(OperateEnum.values())
                                                for (node in children) {
                                                    if (!node.styleClass.contains(SELECTED_OPERATION_STYLE_CLASS)) {
                                                        node.styleClass.add(SELECTED_OPERATION_STYLE_CLASS)
                                                    }
                                                }
                                            }
                                        }
                                },
                            )
                            cursor = Cursor.HAND
                            hgap = 5.0
                            vgap = 5.0
                        }
                    Modal(rootPane, "修改", content, {
                        item.setOperate(selectedOperateEnums)
                    }, {})
                        .apply {
                            isMaskClosable = true
                        }.show()
                }
        }

    private fun saveSetRule() {
        WorkTimeStatus.storeWorkTimeRuleSet(workTimeRuleSetTable.items)
        updateDateComboBoxItems()
    }

    private fun saveApplyRule() {
        val list = dateComboBoxList.toMutableList().apply { removeFirst() }
        val workTimeSetting = list.map { it.value?.id ?: "" }
        WorkTimeStatus.storeWorkTimeSetting(workTimeSetting)
    }

    @FXML
    protected fun save() {
        saveSetRule()
        saveApplyRule()
        notificationManager.showSuccess("保存成功", 2)
    }

    @FXML
    protected fun addRulerSet(actionEvent: ActionEvent) {
        workTimeRuleSetTable.items.add(
            WorkTimeRuleSet("预设${workTimeRuleSetTable.items.size + 1}"),
        )
        updateDateComboBoxItems()
    }

    @FXML
    protected fun delRulerSet(actionEvent: ActionEvent) {
        val index = workTimeRuleSetTable.selectionModel.selectedIndex
        if (index == -1) return
        if (workTimeRuleSetTable.selectionModel.selectedItem.id
                .isEmpty()
        ) {
            notificationManager.showInfo("不允许删除该规则", 2)
            return
        }
        workTimeRuleSetTable.items.removeAt(index)
        updateDateComboBoxItems()
    }

    @FXML
    protected fun copyRulerSet(actionEvent: ActionEvent) {
        val selectedItem = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        val newItem = selectedItem.clone()
        newItem.reGenerateId()
        newItem.setName("副本${workTimeRuleSetTable.items.size + 1}")
        workTimeRuleSetTable.items.add(newItem)
        workTimeRuleSetTable.selectionModel.selectLast()
    }

    @FXML
    protected fun addRuler(actionEvent: ActionEvent) {
        val workTimeRuleSet = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        if (workTimeRuleSet.id.isEmpty()) {
            notificationManager.showInfo("不允许修改该规则", 2)
            return
        }
        val workTimeRule =
            WorkTimeRule(
                WorkTime("00:00", "23:59"),
                setOf<OperateEnum>(OperateEnum.CLOSE_GAME, OperateEnum.CLOSE_PLATFORM),
                true,
            )
        workTimeRuleSet.setTimeRules(workTimeRuleSet.getTimeRules() + workTimeRule)
        selectedWorkTimeRuleTable.items.add(workTimeRule)
        selectedWorkTimeRuleTable.selectionModel.selectLast()
    }

    @FXML
    protected fun delRuler(actionEvent: ActionEvent) {
        val workTimeRule = selectedWorkTimeRuleTable.selectionModel.selectedItem ?: return
        val workTimeRuleSet = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        workTimeRuleSet.setTimeRules(
            workTimeRuleSet.getTimeRules().toMutableList().apply {
                remove(workTimeRule)
            },
        )
        selectedWorkTimeRuleTable.items.remove(workTimeRule)
    }

    @FXML
    protected fun copyRuler(actionEvent: ActionEvent) {
        val selectedItem = selectedWorkTimeRuleTable.selectionModel.selectedItem ?: return
        val selectedRuleSeItem = workTimeRuleSetTable.selectionModel.selectedItem ?: return
        val newItem = selectedItem.clone()
        selectedRuleSeItem.setTimeRules(selectedRuleSeItem.getTimeRules() + newItem)
        selectedWorkTimeRuleTable.items.add(newItem)
        selectedWorkTimeRuleTable.selectionModel.selectLast()
    }
}
