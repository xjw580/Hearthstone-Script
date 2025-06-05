package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.FilterField
import club.xiaojiawei.controls.NumberField
import club.xiaojiawei.func.FilterAction
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox

/**
 * @author 肖嘉威
 * @date 2025/2/27 10:37
 */
class CardField : HBox() {

    @FXML
    lateinit var searchCardField: FilterField

    @FXML
    lateinit var limit: NumberField

    @FXML
    lateinit var offset: NumberField

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/CardField.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        afterLoaded()
    }

    private fun afterLoaded() {
        addListener()
    }

    var searchHandler: ((String?, Int, Int) -> Unit)? = null

    private fun addListener() {
        searchCardField.onFilterAction = FilterAction { text: String? ->
            search()
            searchCardField.requestFocus()
            searchCardField.selectAll()
        }
        limit.addEventFilter(
            KeyEvent.KEY_RELEASED
        ) { event: KeyEvent ->
            if (event.code == KeyCode.ENTER) {
                search()
                limit.requestFocus()
                limit.selectAll()
            }
        }
        offset.addEventFilter(
            KeyEvent.KEY_RELEASED
        ) { event: KeyEvent ->
            if (event.code == KeyCode.ENTER) {
                search()
                offset.requestFocus()
                offset.selectAll()
            }
        }
    }

    private fun search() {
        val text = searchCardField.text
        if (text == null || text.isEmpty()) {
            searchHandler?.invoke(null, 0, 0)
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
        searchHandler?.invoke(text, limit, offset)
    }

}