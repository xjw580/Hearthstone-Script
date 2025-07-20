package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.TableFilterManagerGroup
import club.xiaojiawei.data.BaseData
import club.xiaojiawei.hsscript.bean.WeightCard
import club.xiaojiawei.hsscript.component.CardTableView
import club.xiaojiawei.hsscript.consts.CARD_WEIGHT_CONFIG_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.CardUtil
import club.xiaojiawei.hsscript.utils.CardUtil.getCardWeightCache
import club.xiaojiawei.hsscript.utils.CardUtil.reloadCardWeight
import club.xiaojiawei.hsscript.utils.CardUtil.saveWeightConfig
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.tablecell.NumberFieldTableCellUI
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
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

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var cardTable: CardTableView

    @FXML
    protected lateinit var weightTable: TableView<WeightCard>

    @FXML
    protected lateinit var weightTableProxy: TableFilterManagerGroup<WeightCard, WeightCard>

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
    protected lateinit var changeWeightCol: TableColumn<WeightCard, Number?>

    @FXML
    protected lateinit var changeCheckBox: CheckBox

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initTable()
        val cards = getCardWeightCache()
        if (cards != null) {
            val weightCards = HashSet(weightTable.items)
            for (card in cards) {
                if (!weightCards.contains(card)) {
                    weightTable.items.add(card)
                }
            }
        }
        changeCheckBox.isSelected = ConfigUtil.getBoolean(ConfigEnum.ENABLE_CHANGE_WEIGHT)
        changeWeightCol.isVisible = changeCheckBox.isSelected
        changeCheckBox.selectedProperty().addListener { observable, oldValue, newValue ->
            ConfigUtil.putBoolean(ConfigEnum.ENABLE_CHANGE_WEIGHT, newValue)
            BaseData.enableChangeWeight = newValue
            changeWeightCol.isVisible = newValue
        }
    }

    private fun initTable() {
        val numberConverter: StringConverter<Number> = object : StringConverter<Number>() {
            override fun toString(number: Number): String {
                return number.toString()
            }

            override fun fromString(s: String?): Number {
                return if (s.isNullOrBlank()) 0.0 else s.toDouble()
            }
        }
        val stringConverter: StringConverter<String?> = object : StringConverter<String?>() {
            override fun toString(`object`: String?): String? {
                return `object`
            }

            override fun fromString(string: String?): String? {
                return string
            }
        }

        cardTable.notificationManager = this.notificationManager

        weightTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
        weightTable.isEditable = true
        weightNoCol.setCellValueFactory { param: TableColumn.CellDataFeatures<WeightCard, Number?> ->
            val items = param.tableView.items
            val index =
                IntStream.range(0, items.size).filter { i: Int -> items[i] === param.value }.findFirst().orElse(-2)
            SimpleIntegerProperty(index + 1)
        }
        weightCardIdCol.setCellValueFactory { it.value.cardIdProperty }
        weightCardIdCol.setCellFactory {
            object : TextFieldTableCellUI<WeightCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改ID成功", 1)
                }
            }
        }
        weightNameCol.setCellValueFactory { it.value.nameProperty }
        weightNameCol.setCellFactory {
            object : TextFieldTableCellUI<WeightCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改名字成功", 1)
                }
            }
        }
        weightCol.setCellValueFactory { it.value.weightProperty }
        weightCol.setCellFactory {
            object : NumberFieldTableCellUI<WeightCard?, Number>(numberConverter) {
                override fun commitEdit(number: Number) {
                    super.commitEdit(number)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改权重成功", 1)
                }
            }
        }
        powerWeightCol.setCellValueFactory { it.value.powerWeightProperty }
        powerWeightCol.setCellFactory {
            object : NumberFieldTableCellUI<WeightCard?, Number>(numberConverter) {
                override fun commitEdit(number: Number) {
                    super.commitEdit(number)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改权重成功", 1)
                }
            }
        }
        changeWeightCol.setCellValueFactory { it.value.changeWeightProperty }
        changeWeightCol.setCellFactory {
            object : NumberFieldTableCellUI<WeightCard?, Number>(numberConverter) {
                override fun commitEdit(number: Number) {
                    super.commitEdit(number)
                    saveWeightConfig()
                    notificationManager.showSuccess("修改权重成功", 1)
                }
            }
        }
    }

    private fun readWeightConfig(weightPath: Path = CARD_WEIGHT_CONFIG_PATH) {
        val cards = CardUtil.readWeightConfig(weightPath)
        val weightCards = HashSet(weightTable.items)
        for (card in cards) {
            if (!weightCards.contains(card)) {
                weightTable.items.add(card)
            }
        }
    }

    private fun saveWeightConfig(weightPath: Path = CARD_WEIGHT_CONFIG_PATH) {
        saveWeightConfig(weightTable.items, weightPath)
        reloadCardWeight(weightTable.items)
    }

    @FXML
    protected fun exportConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "导出至"
        val extFilter = FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight")
        chooser.extensionFilters.add(extFilter)

        val file = chooser.showSaveDialog(rootPane.scene.window)
        if (file == null) {
            notificationManager.showInfo("未选择导出路径，导出取消", 1)
            return
        }
        saveWeightConfig(file.toPath())
        notificationManager.showSuccess("导出成功", 1)
    }

    @FXML
    protected fun importConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "选择要导入的权重文件"
        val extFilter = FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight")
        chooser.extensionFilters.add(extFilter)

        val files = chooser.showOpenMultipleDialog(rootPane.scene.window)
        if (files == null || files.isEmpty()) {
            notificationManager.showInfo("未选择导入路径，导入取消", 1)
            return
        }
        for (file in files) {
            readWeightConfig(file.toPath())
        }
        saveWeightConfig()
        notificationManager.showSuccess("导入成功", 1)
    }

    @FXML
    protected fun copyRow(actionEvent: ActionEvent?) {
        val selectedItems = weightTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("请先选择要复制的行", 1)
            return
        }
        val selectedItemsCopy = selectedItems.toList()
        weightTable.selectionModel.clearSelection()
        for (weightCard in selectedItemsCopy) {
            weightTable.items.add(weightCard.clone())
        }
        saveWeightConfig()
        notificationManager.showSuccess("复制成功", 1)
    }

    @FXML
    protected fun addWeight(actionEvent: ActionEvent?) {
        val selectedItems = cardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("左边数据表没有选中行", 1)
            return
        }
        val list = ArrayList(selectedItems)
        for ((cardId, name, attack, health, cost) in list) {
            val weightCard = WeightCard(
                cardId, name, 1.0, 1.0,
                if (cost == null || cost > 2) -1.0 else 0.0
            )
            weightTable.items.add(weightCard)
        }
        saveWeightConfig()
        notificationManager.showSuccess("添加成功", 1)

        weightTable.scrollTo(weightTable.items.size - 1)
        weightTable.selectionModel.clearSelection()
        weightTable.selectionModel.selectLast()
    }

    @FXML
    protected fun removeWeight(actionEvent: ActionEvent?) {
        val selectedItems = weightTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("右边权重表没有选中行", 1)
            return
        }
        val weightCards = ArrayList(selectedItems)
        weightTable.items.removeAll(weightCards)
        saveWeightConfig()
        notificationManager.showSuccess("移除成功", 1)
    }

}