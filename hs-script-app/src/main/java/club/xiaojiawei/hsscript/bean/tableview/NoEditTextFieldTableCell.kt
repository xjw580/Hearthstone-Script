package club.xiaojiawei.hsscript.bean.tableview

import club.xiaojiawei.tablecell.TextFieldTableCellUI
import javafx.scene.control.TextField
import javafx.util.StringConverter

/**
 * @author 肖嘉威
 * @date 2025/4/8 17:04
 */
open class NoEditTextFieldTableCell<S, T>(stringConverter: StringConverter<T>?) :
    TextFieldTableCellUI<S, T>(stringConverter) {
    override fun startEdit() {
        super.startEdit()
        (graphic as TextField).isEditable = false
    }
}