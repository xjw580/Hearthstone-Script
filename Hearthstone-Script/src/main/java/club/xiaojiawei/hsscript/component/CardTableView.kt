package club.xiaojiawei.hsscript.component

import club.xiaojiawei.bean.DBCard
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.hsscript.bean.tableview.NoEditTextFieldTableCell
import club.xiaojiawei.util.CardDBUtil
import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.StringConverter
import java.util.stream.IntStream


/**
 * @author 肖嘉威
 * @date 2025/2/27 9:00
 */
class CardTableView : TableView<DBCard>() {

    @FXML
    protected var colMinWeight: Double = 0.0

    @FXML
    lateinit var noCol: TableColumn<DBCard, Number?>

    @FXML
    lateinit var cardIdCol: TableColumn<DBCard, String>

    @FXML
    lateinit var nameCol: TableColumn<DBCard, String>

    @FXML
    lateinit var attackCol: TableColumn<DBCard, Number>

    @FXML
    lateinit var healthCol: TableColumn<DBCard, Number>

    @FXML
    lateinit var costCol: TableColumn<DBCard, Number>

    @FXML
    lateinit var textCol: TableColumn<DBCard, String>

    @FXML
    lateinit var typeCol: TableColumn<DBCard, String>

    @FXML
    lateinit var cardSetCol: TableColumn<DBCard, String>

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/CardTableView.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        afterLoaded()
    }

    var notificationManager: NotificationManager<String>? = null

    var currentOffset = 0

    private fun afterLoaded() {
        initTable()
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
        this.selectionModel.selectionMode = SelectionMode.MULTIPLE
        this.isEditable = true
        noCol.setCellValueFactory { param: TableColumn.CellDataFeatures<DBCard, Number?> ->
            val items = param.tableView.items
            val index =
                IntStream.range(0, items.size).filter { i: Int -> items[i] === param.value }.findFirst().orElse(-2)
            SimpleIntegerProperty(index + 1 + currentOffset)
        }
        noCol.text = "#"
        noCol.maxWidth = 30.0
        cardIdCol.setCellValueFactory(PropertyValueFactory("cardId"))
        cardIdCol.setCellFactory { weightCardNumberTableColumn: TableColumn<DBCard, String>? ->
            object : NoEditTextFieldTableCell<DBCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager?.showInfo("不允许修改", 1)
                }
            }
        }
        nameCol.setCellValueFactory(PropertyValueFactory("name"))
        nameCol.setCellFactory { weightCardNumberTableColumn: TableColumn<DBCard, String>? ->
            object : NoEditTextFieldTableCell<DBCard?, String?>(stringConverter) {
                override fun commitEdit(s: String?) {
                    super.commitEdit(s)
                    notificationManager?.showInfo("不允许修改", 1)
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
                    notificationManager?.showInfo("不允许修改", 1)
                }
            }
        }
        typeCol.setCellValueFactory(PropertyValueFactory("type"))
        cardSetCol.setCellValueFactory(PropertyValueFactory("cardSet"))
    }

    fun setCardByName(name: String, limit: Int, offset: Int) {
        this.currentOffset = offset
        this.items.setAll(CardDBUtil.queryCardByName(name, limit, offset, false))
    }

    fun setCardById(id: String, limit: Int, offset: Int) {
        this.currentOffset = offset
        this.items.setAll(CardDBUtil.queryCardById(id, limit, offset, false))
    }

    fun addCardByName(name: String, limit: Int, offset: Int) {
        this.items.addAll(CardDBUtil.queryCardByName(name, limit, offset, false))
    }

    fun addCardById(id: String, limit: Int, offset: Int) {
        this.items.addAll(CardDBUtil.queryCardById(id, limit, offset, false))
    }

}