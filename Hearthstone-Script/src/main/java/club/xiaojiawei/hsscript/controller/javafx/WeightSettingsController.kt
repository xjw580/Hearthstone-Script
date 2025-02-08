package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.bean.DBCard
import club.xiaojiawei.controls.FilterField
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.NumberField
import club.xiaojiawei.func.FilterAction
import club.xiaojiawei.hsscript.bean.WeightCard
import club.xiaojiawei.hsscript.data.CONFIG_PATH
import club.xiaojiawei.hsscript.utils.CardUtil
import club.xiaojiawei.hsscript.utils.CardUtil.getCardWeightCache
import club.xiaojiawei.hsscript.utils.CardUtil.reloadCardWeight
import club.xiaojiawei.hsscript.utils.CardUtil.saveWeightConfig
import club.xiaojiawei.tablecell.NumberFieldTableCellUI
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import club.xiaojiawei.util.CardDBUtil.queryCardByName
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.util.StringConverter
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.stream.IntStream

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class WeightSettingsController : Initializable {
    //    private static final Logger log = LoggerFactory.getLogger(WeightSettingsController.class);
    @FXML
    protected lateinit var limit: NumberField

    @FXML
    protected lateinit var offset: NumberField

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var cardTable: TableView<DBCard>

    @FXML
    protected lateinit var noCol: TableColumn<DBCard, Number?>

    @FXML
    protected lateinit var cardIdCol: TableColumn<DBCard, String>

    @FXML
    protected lateinit var nameCol: TableColumn<DBCard, String>

    @FXML
    protected lateinit var attackCol: TableColumn<DBCard, Number>

    @FXML
    protected lateinit var healthCol: TableColumn<DBCard, Number>

    @FXML
    protected lateinit var costCol: TableColumn<DBCard, Number>

    @FXML
    protected lateinit var textCol: TableColumn<DBCard, String>

    @FXML
    protected lateinit var typeCol: TableColumn<DBCard, String>

    @FXML
    protected lateinit var cardSetCol: TableColumn<DBCard, String>

    @FXML
    protected lateinit var weightTable: TableView<WeightCard>

    @FXML
    protected lateinit var weightNoCol: TableColumn<WeightCard, Number?>

    @FXML
    protected lateinit var weightCardIdCol: TableColumn<WeightCard, String>

    @FXML
    protected lateinit var weightNameCol: TableColumn<WeightCard, String>

    @FXML
    protected lateinit var weightCol: TableColumn<WeightCard, Number?>

    @FXML
    protected lateinit var powerWeightCol: TableColumn<WeightCard, Number?>

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    @FXML
    protected lateinit var searchCardField: FilterField

    private var currentOffset = 0

    internal open class NoEditTextFieldTableCell<S, T>(stringConverter: StringConverter<T>?) :
        TextFieldTableCellUI<S, T>(stringConverter) {
        override fun startEdit() {
            super.startEdit()
            (graphic as TextField).isEditable = false
        }
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initTable()
        addListener()
        val cards = getCardWeightCache()
        if (cards != null) {
            val weightCards = HashSet(weightTable.items)
            for (card in cards) {
                if (!weightCards.contains(card)) {
                    weightTable.items.add(card)
                }
            }
        }
    }

    private fun search() {
        val text = searchCardField.text
        if (text == null || text.isEmpty()) {
            cardTable.items.clear()
            return
        }
        val limit = if (limit.text.isBlank()) {
            limit.promptText.toInt()
        } else {
            limit.text.toInt()
        }
        val offset = if (offset.text.isBlank()) {
            offset.promptText.toInt()
        } else {
            offset.text.toInt()
        }
        currentOffset = offset
        cardTable.items.setAll(queryCardByName(text, limit, offset, false))
    }

    private fun addListener() {
        searchCardField.onFilterAction = FilterAction { text: String? ->
            search()
        }
        weightTable.selectionModel.selectedItemProperty()
            .addListener { observable: ObservableValue<out WeightCard>?, oldValue: WeightCard?, newValue: WeightCard? ->
                if (newValue != null) {
                    searchCardField.text = newValue.name
                }
            }
        limit.addEventFilter(
            KeyEvent.KEY_RELEASED
        ) { event: KeyEvent ->
            if (event.code == KeyCode.ENTER) {
                search()
            }
        }
        offset.addEventFilter(
            KeyEvent.KEY_RELEASED
        ) { event: KeyEvent ->
            if (event.code == KeyCode.ENTER) {
                search()
            }
        }
    }

    private fun initTable() {
        val stringConverter: StringConverter<String?> = object : StringConverter<String?>() {
            override fun toString(`object`: String?): String? {
                return `object`
            }

            override fun fromString(string: String?): String? {
                return string
            }
        }
        val numberConverter: StringConverter<Number> = object : StringConverter<Number>() {
            override fun toString(number: Number): String {
                return number.toString()
            }

            override fun fromString(s: String?): Number {
                return if (s.isNullOrBlank()) 0.0 else s.toDouble()
            }
        }
        cardTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
        cardTable.isEditable = true
        noCol.setCellValueFactory { param: TableColumn.CellDataFeatures<DBCard, Number?> ->
            val items = param.tableView.items
            val index =
                IntStream.range(0, items.size).filter { i: Int -> items[i] === param.value }.findFirst().orElse(-2)
            SimpleIntegerProperty(index + 1 + currentOffset)
        }
        cardIdCol.setCellValueFactory(PropertyValueFactory("cardId"))
        cardIdCol.setCellFactory { weightCardNumberTableColumn: TableColumn<DBCard, String>? ->
            object : NoEditTextFieldTableCell<DBCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager.showInfo("不允许修改", 1)
                }
            }
        }
        nameCol.setCellValueFactory(PropertyValueFactory("name"))
        nameCol.setCellFactory { weightCardNumberTableColumn: TableColumn<DBCard, String>? ->
            object : NoEditTextFieldTableCell<DBCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager.showInfo("不允许修改", 1)
                }
            }
        }
        attackCol.setCellValueFactory(PropertyValueFactory("attack"))
        healthCol.setCellValueFactory(PropertyValueFactory("health"))
        costCol.setCellValueFactory(PropertyValueFactory("cost"))
        textCol.setCellValueFactory(PropertyValueFactory("text"))
        textCol.setCellFactory { weightCardNumberTableColumn: TableColumn<DBCard, String>? ->
            object : NoEditTextFieldTableCell<DBCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager.showInfo("不允许修改", 1)
                }
            }
        }
        typeCol.setCellValueFactory(PropertyValueFactory("type"))
        cardSetCol.setCellValueFactory(PropertyValueFactory("cardSet"))

        weightTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
        weightTable.isEditable = true
        weightNoCol.setCellValueFactory { param: TableColumn.CellDataFeatures<WeightCard, Number?> ->
            val items = param.tableView.items
            val index =
                IntStream.range(0, items.size).filter { i: Int -> items[i] === param.value }.findFirst().orElse(-2)
            SimpleIntegerProperty(index + 1 + currentOffset)
        }
        weightCardIdCol.setCellValueFactory(PropertyValueFactory("cardId"))
        weightCardIdCol.setCellFactory { weightCardNumberTableColumn: TableColumn<WeightCard, String>? ->
            object : TextFieldTableCellUI<WeightCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    s?.let {
                        weightTable.items[index].cardId = it
                    }
                    saveWeightConfig()
                    notificationManager.showSuccess("修改ID成功", 2)
                }
            }
        }
        weightNameCol.setCellValueFactory(PropertyValueFactory("name"))
        weightNameCol.setCellFactory { weightCardNumberTableColumn: TableColumn<WeightCard, String>? ->
            object : NoEditTextFieldTableCell<WeightCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager.showInfo("不允许修改", 1)
                }
            }
        }
        weightCol.setCellValueFactory { o: TableColumn.CellDataFeatures<WeightCard, Number?> -> o.value.weightProperty }
        weightCol.setCellFactory { weightCardNumberTableColumn: TableColumn<WeightCard, Number?>? ->
            object : NumberFieldTableCellUI<WeightCard?, Number>(numberConverter) {
                override fun commitEdit(number: Number) {
                    super.commitEdit(number)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改权重成功", 2)
                }
            }
        }
        powerWeightCol.setCellValueFactory { o: TableColumn.CellDataFeatures<WeightCard, Number?> -> o.value.powerWeightProperty }
        powerWeightCol.setCellFactory { weightCardNumberTableColumn: TableColumn<WeightCard, Number?>? ->
            object : NumberFieldTableCellUI<WeightCard?, Number>(numberConverter) {
                override fun commitEdit(number: Number) {
                    super.commitEdit(number)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改权重成功", 2)
                }
            }
        }
    }

    private fun readWeightConfig(weigthPath: Path = WEIGHT_CONFIG_PATH) {
        val cards = CardUtil.readWeightConfig(weigthPath)
        val weightCards = HashSet(weightTable.items)
        for (card in cards) {
            if (!weightCards.contains(card)) {
                weightTable.items.add(card)
            }
        }
    }

    private fun saveWeightConfig(weigthPath: Path = WEIGHT_CONFIG_PATH) {
        saveWeightConfig(weightTable.items, weigthPath)
        reloadCardWeight(weightTable.items)
    }

    @FXML
    protected fun importConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "选择要导入的权重文件"
        val extFilter = FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight")
        chooser.extensionFilters.add(extFilter)

        val files = chooser.showOpenMultipleDialog(rootPane.scene.window)
        if (files == null || files.isEmpty()) {
            notificationManager.showInfo("未选择导入路径，导入取消", 2)
            return
        }
        for (file in files) {
            readWeightConfig(file.toPath())
        }
        saveWeightConfig()
        notificationManager.showSuccess("导入成功", 2)
    }

    @FXML
    protected fun exportConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "导出至"
        val extFilter = FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight")
        chooser.extensionFilters.add(extFilter)

        val file = chooser.showSaveDialog(rootPane.scene.window)
        if (file == null) {
            notificationManager.showInfo("未选择导出路径，导出取消", 2)
            return
        }
        saveWeightConfig(file.toPath())
        notificationManager.showSuccess("导出成功", 2)
    }

    @FXML
    protected fun addWeight(actionEvent: ActionEvent?) {
        val selectedItems = cardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("左边数据表没有选中行", 2)
            return
        }
        val list = ArrayList(selectedItems)
        val weightSet = HashSet(weightTable.items)
        var hasUpdate = false
        for ((cardId, name) in list) {
            val weightCard = WeightCard(cardId, name, 1.0, 1.0)
            if (weightSet.contains(weightCard)) {
                hasUpdate = true
            } else {
                weightTable.items.add(weightCard)
            }
        }
        saveWeightConfig()
        notificationManager.showSuccess(if (hasUpdate) "更新成功" else "添加成功", 2)
    }

    @FXML
    protected fun removeWeight(actionEvent: ActionEvent?) {
        val selectedItems = weightTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("右边权重表没有选中行", 2)
            return
        }
        val weightCards = ArrayList(selectedItems)
        weightTable.selectionModel.clearSelection()
        weightTable.items.removeAll(weightCards)
        saveWeightConfig()
        notificationManager.showSuccess("移除成功", 2)
    }

    companion object {
        private val WEIGHT_CONFIG_PATH: Path = Path.of(CONFIG_PATH, "card.weight")
    }
}