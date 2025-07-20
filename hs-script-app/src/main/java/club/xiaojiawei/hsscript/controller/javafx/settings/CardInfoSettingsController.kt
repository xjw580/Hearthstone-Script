package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ico.EditIco
import club.xiaojiawei.enums.CardActionEnum
import club.xiaojiawei.enums.CardEffectTypeEnum
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.hsscript.bean.InfoCard
import club.xiaojiawei.hsscript.bean.tableview.ComboBoxTableCell
import club.xiaojiawei.hsscript.bean.tableview.NoEditTextFieldTableCell
import club.xiaojiawei.hsscript.component.CardTableView
import club.xiaojiawei.hsscript.component.EditActionPane
import club.xiaojiawei.hsscript.consts.CARD_INFO_CONFIG_PATH
import club.xiaojiawei.hsscript.enums.CardInfoActionTypeEnum
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.utils.CardUtil
import club.xiaojiawei.hsscript.utils.MenuItemUtil
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Popup
import javafx.util.StringConverter
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.stream.IntStream

/**
 * @author 肖嘉威
 * @date 2025/6/9 12:24
 */
class CardInfoSettingsController : Initializable, StageHook {

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    @FXML
    protected lateinit var infoCardEffectTypeCol: TableColumn<InfoCard, CardEffectTypeEnum>

    @FXML
    protected lateinit var playActionCol: TableColumn<InfoCard, List<CardActionEnum>>

    @FXML
    protected lateinit var powerActionCol: TableColumn<InfoCard, List<CardActionEnum>>

    @FXML
    protected lateinit var actionCardNameCol: TableColumn<InfoCard, String>

    @FXML
    protected lateinit var cardInfoIdColCard: TableColumn<InfoCard, String>

    @FXML
    protected lateinit var infoCardNoCol: TableColumn<InfoCard, Number>

    @FXML
    protected lateinit var infoCardTable: TableView<InfoCard>

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var cardTable: CardTableView

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        initTable()
        val cards = CardUtil.getCardInfoCache()
        if (cards != null) {
            val infoCards = HashSet(infoCardTable.items)
            for (card in cards) {
                if (!infoCards.contains(card)) {
                    infoCardTable.items.add(card)
                }
            }
        }

    }

    private fun initTable() {
        cardTable.notificationManager = this.notificationManager
        val stringConverter: StringConverter<String?> = object : StringConverter<String?>() {
            override fun toString(`object`: String?): String? {
                return `object`
            }

            override fun fromString(string: String?): String? {
                return string
            }
        }

        infoCardTable.contextMenu =
            ContextMenu(MenuItemUtil.format(MenuItem(), "编辑${CardInfoActionTypeEnum.PLAY.comment}", EditIco()).apply {
                setOnAction {
                    val selectedItem = infoCardTable.selectionModel.selectedItem
                    if (selectedItem == null) {
                        notificationManager.showInfo("请先选择要编辑的行", 1)
                        return@setOnAction
                    }
                    editAction(selectedItem, CardInfoActionTypeEnum.PLAY)
                }
            }, MenuItemUtil.format(MenuItem(), "编辑${CardInfoActionTypeEnum.POWER.comment}", EditIco()).apply {
                setOnAction {
                    val selectedItem = infoCardTable.selectionModel.selectedItem
                    if (selectedItem == null) {
                        notificationManager.showInfo("请先选择要编辑的行", 1)
                        return@setOnAction
                    }
                    editAction(selectedItem, CardInfoActionTypeEnum.POWER)
                }
            }).apply {
                styleClass.add("context-menu-ui")
            }
        infoCardTable.selectionModel.selectionMode = SelectionMode.MULTIPLE
        infoCardTable.isEditable = true
        infoCardNoCol.setCellValueFactory { param: TableColumn.CellDataFeatures<InfoCard, Number?> ->
            val items = param.tableView.items
            val index =
                IntStream.range(0, items.size).filter { i: Int -> items[i] === param.value }.findFirst().orElse(-2)
            SimpleIntegerProperty(index + 1)
        }
        cardInfoIdColCard.setCellValueFactory { it.value.cardIdProperty }
        cardInfoIdColCard.setCellFactory { weightCardNumberTableColumn: TableColumn<InfoCard, String>? ->
            object : TextFieldTableCellUI<InfoCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    saveConfig()
                    notificationManager.showSuccess("修改ID成功", 1)
                }
            }
        }
        actionCardNameCol.setCellValueFactory { it.value.nameProperty }
        actionCardNameCol.setCellFactory {
            object : TextFieldTableCellUI<InfoCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    saveConfig()
                    notificationManager.showSuccess("修改名字成功", 1)
                }
            }
        }
        infoCardEffectTypeCol.setCellValueFactory { it.value.effectTypeProperty }
        infoCardEffectTypeCol.setCellFactory {
            object : ComboBoxTableCell<InfoCard?, CardEffectTypeEnum?>(*CardEffectTypeEnum.entries.toTypedArray()) {
                override fun comboBoxStyleClass(): MutableList<String?> {
                    val comboBoxStyleClass = super.comboBoxStyleClass()
                    comboBoxStyleClass.add("combo-box-ui-small")
                    return comboBoxStyleClass
                }

                override fun commitEdit(p0: CardEffectTypeEnum?) {
                    super.commitEdit(p0)
                    saveConfig()
                }
            }
        }
        val actionConverter = object : StringConverter<List<CardActionEnum>?>() {
            override fun toString(`object`: List<CardActionEnum>?): String? {
                return `object`?.joinToString(",") { it.comment }
            }

            override fun fromString(string: String?): List<CardActionEnum>? {
                return null
            }
        }
        playActionCol.setCellValueFactory { it.value.playActionsProperty }
        playActionCol.setCellFactory {
            object : NoEditTextFieldTableCell<InfoCard?, List<CardActionEnum>?>(actionConverter) {
                override fun startEdit() {
                    editAction(infoCardTable.items[index], CardInfoActionTypeEnum.PLAY)
                }
            }
        }
        powerActionCol.setCellValueFactory { it.value.powerActionsProperty }
        powerActionCol.setCellFactory {
            object : NoEditTextFieldTableCell<InfoCard?, List<CardActionEnum>?>(actionConverter) {
                override fun startEdit() {
                    editAction(infoCardTable.items[index], CardInfoActionTypeEnum.POWER)
                }
            }
        }
        infoCardTable.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue ?: return@addListener
            updatePopup(newValue, CardInfoActionTypeEnum.PLAY)
        }
    }

    private var editActionPopup: Popup? = null

    private var editActionPane: EditActionPane? = null

    private fun updatePopup(infoCard: InfoCard, type: CardInfoActionTypeEnum) {
        editActionPane?.let { editActionPane ->
            editActionPane.setTitle("修改[${infoCard.name}]的${type.comment}")
            editActionPane.infoCard = infoCard
            editActionPane.actionTypeEnum = type
            editActionPane.update()
        }
    }

    private fun editAction(infoCard: InfoCard, type: CardInfoActionTypeEnum) {
        val popup = editActionPopup?.let { it ->
            updatePopup(infoCard, type)
            it
        } ?: let {
            val popup = Popup()
            val editActionPane = EditActionPane(infoCard, type) {
                saveConfig()
                val nextSelectedIndex = infoCardTable.selectionModel.selectedIndex + 1
                if (nextSelectedIndex < infoCardTable.items.size - 1) {
                    infoCardTable.selectionModel.clearAndSelect(nextSelectedIndex)
                }
            }
            editActionPane.setTitle("修改[${infoCard.name}]的${type.comment}")
            this.editActionPane = editActionPane
            popup.content.add(editActionPane)
            popup
        }
        editActionPopup = popup
        popup.show(rootPane.scene.window)
    }

    private fun saveConfig(path: Path = CARD_INFO_CONFIG_PATH) {
        CardUtil.saveInfoConfig(infoCardTable.items, path)
        CardUtil.reloadCardInfo(infoCardTable.items)
    }

    @FXML
    protected fun addItem() {
        val selectedItems = cardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("左边数据表没有选中行", 1)
            return
        }
        val list = ArrayList(selectedItems)
        for (dbCard in list) {
            dbCard.run {
                val infoCard = InfoCard(
                    cardId,
                    name,
                    playActions = listOf(CardActionEnum.NO_POINT),
                    powerActions = listOf(CardActionEnum.NO_POINT)
                )
                if (type == CardTypeEnum.MINION.name || type == CardTypeEnum.HERO.name || type == CardTypeEnum.WEAPON.name) {
                    infoCard.powerActions = listOf(CardActionEnum.POINT_RIVAL)
                } else if (type == CardTypeEnum.SPELL.name) {
                    infoCard.powerActions = emptyList()
                }
                infoCardTable.items.add(infoCard)
            }
        }
        saveConfig()
        notificationManager.showSuccess("添加成功", 1)
        infoCardTable.scrollTo(infoCardTable.items.size - 1)
        infoCardTable.selectionModel.clearSelection()
        infoCardTable.selectionModel.selectLast()
    }

    @FXML
    protected fun removeItem() {
        val selectedItems = infoCardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("右边表没有选中行", 1)
            return
        }
        val weightCards = ArrayList(selectedItems)
        infoCardTable.items.removeAll(weightCards)
        saveConfig()
        notificationManager.showSuccess("移除成功", 1)
    }

    @FXML
    protected fun exportConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "导出至"
        val extFilter = FileChooser.ExtensionFilter("卡牌信息文件 (*.info)", "*.info")
        chooser.extensionFilters.add(extFilter)

        val file = chooser.showSaveDialog(rootPane.scene.window)
        if (file == null) {
            notificationManager.showInfo("未选择导出路径，导出取消", 1)
            return
        }
        saveConfig(file.toPath())
        notificationManager.showSuccess("导出成功", 1)
    }

    @FXML
    protected fun importConfig(actionEvent: ActionEvent?) {
        val chooser = FileChooser()
        chooser.title = "选择要导入的卡牌信息文件"
        val extFilter = FileChooser.ExtensionFilter("卡牌信息文件 (*.info)", "*.info")
        chooser.extensionFilters.add(extFilter)

        val files = chooser.showOpenMultipleDialog(rootPane.scene.window)
        if (files == null || files.isEmpty()) {
            notificationManager.showInfo("未选择导入路径，导入取消", 1)
            return
        }
        for (file in files) {
            readCardInfoConfig(file.toPath())
        }
        saveConfig()
        notificationManager.showSuccess("导入成功", 1)
    }

    private fun readCardInfoConfig(weightPath: Path = CARD_INFO_CONFIG_PATH) {
        val cards = CardUtil.readCardInfoConfig(weightPath)
        val infoCards = HashSet(infoCardTable.items)
        for (card in cards) {
            if (!infoCards.contains(card)) {
                infoCardTable.items.add(card)
            }
        }
    }

    @FXML
    protected fun copyRow() {
        val selectedItems = infoCardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("请先选择要复制的行", 1)
            return
        }
        val selectedItemsCopy = selectedItems.toList()
        infoCardTable.selectionModel.clearSelection()
        for (infoCard in selectedItemsCopy) {
            infoCardTable.items.add(infoCard.clone())
        }
        saveConfig()
        notificationManager.showSuccess("复制成功", 1)
    }

    override fun onHidden() {
        editActionPopup?.hide()
    }
}
