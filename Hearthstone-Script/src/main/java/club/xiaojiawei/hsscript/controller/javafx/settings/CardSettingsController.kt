package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ico.EditIco
import club.xiaojiawei.hsscript.bean.InfoCard
import club.xiaojiawei.hsscript.bean.tableview.ComboBoxTableCell
import club.xiaojiawei.hsscript.bean.tableview.NoEditTextFieldTableCell
import club.xiaojiawei.hsscript.component.CardTableView
import club.xiaojiawei.hsscript.consts.CARD_INFO_CONFIG_PATH
import club.xiaojiawei.hsscript.enums.CardEffectTypeEnum
import club.xiaojiawei.hsscript.utils.CardUtil
import club.xiaojiawei.hsscript.utils.MenuItemUtil
import club.xiaojiawei.tablecell.TextFieldTableCellUI
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.util.StringConverter
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.stream.IntStream

/**
 * @author 肖嘉威
 * @date 2025/6/9 12:24
 */
class CardSettingsController : Initializable {

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    @FXML
    protected lateinit var infoCardEffectTypeCol: TableColumn<InfoCard, CardEffectTypeEnum>

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

        infoCardTable.contextMenu = ContextMenu(
            MenuItemUtil.format(MenuItem(), "编辑行为", EditIco()).apply {
                onAction = EventHandler {
                    val selectedItems = infoCardTable.selectionModel.selectedItems
                    if (selectedItems.isEmpty()) {
                        notificationManager.showInfo("请先选择要编辑的行")
                        return@EventHandler
                    }
                }
            }
        ).apply {
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
                    notificationManager.showSuccess("修改ID成功", 2)
                }
            }
        }
        actionCardNameCol.setCellValueFactory { it.value.nameProperty }
        actionCardNameCol.setCellFactory {
            object : NoEditTextFieldTableCell<InfoCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager.showInfo("不允许修改", 1)
                }
            }
        }
        infoCardEffectTypeCol.setCellValueFactory { it.value.effectTypeProperty }
        infoCardEffectTypeCol.setCellFactory {
            ComboBoxTableCell(*CardEffectTypeEnum.entries.toTypedArray())
        }
    }

    private fun saveConfig(path: Path = CARD_INFO_CONFIG_PATH) {
        CardUtil.saveInfoConfig(infoCardTable.items, path)
        CardUtil.reloadCardInfo(infoCardTable.items)
    }

    @FXML
    protected fun addItem() {
        val selectedItems = cardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("左边数据表没有选中行", 2)
            return
        }
        val list = ArrayList(selectedItems)
        val cardInfoSet = HashSet(infoCardTable.items)
        var hasUpdate = false
        for ((cardId, name) in list) {
            val infoCard = InfoCard(cardId, name)
            if (cardInfoSet.contains(infoCard)) {
                hasUpdate = true
            } else {
                infoCardTable.items.add(infoCard)
            }
        }
        saveConfig()
        notificationManager.showSuccess(if (hasUpdate) "更新成功" else "添加成功", 2)
    }

    @FXML
    protected fun removeItem() {
        val selectedItems = infoCardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("右边表没有选中行", 2)
            return
        }
        val weightCards = ArrayList(selectedItems)
        infoCardTable.selectionModel.clearSelection()
        infoCardTable.items.removeAll(weightCards)
        saveConfig()
        notificationManager.showSuccess("移除成功", 2)
    }

    @FXML
    protected fun exportConfig() {

    }

    @FXML
    protected fun importConfig() {

    }

    @FXML
    protected fun copyRow() {
        val selectedItems = infoCardTable.selectionModel.selectedItems
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("请先选择要复制的行", 2)
            return
        }
        val selectedItemsCopy = selectedItems.toList()
        infoCardTable.selectionModel.clearSelection()
        for (infoCard in selectedItemsCopy) {
            infoCardTable.items.add(infoCard.clone())
        }
        saveConfig()
        notificationManager.showSuccess("复制成功", 2)
    }

}
