package club.xiaojiawei.hsscript.component

import club.xiaojiawei.hsscript.utils.SystemUtil
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Hyperlink

/**
 * @author 肖嘉威
 * @date 2025/2/10 20:40
 */
class UrlLabel() : Hyperlink() {

    private var _url: StringProperty = SimpleStringProperty()

    var url: String?
        set(value) {
            _url.set(value)
        }
        get() {
            return _url.get()
        }

    private var _file: StringProperty = SimpleStringProperty()

    var file: String?
        set(value) {
            _file.set(value)
        }
        get() {
            return _file.get()
        }


    init {
        setOnAction {
            url?.let {
                SystemUtil.openURL(it)
            }
            file?.let {
                SystemUtil.openFile(it)
            }
        }
    }

}