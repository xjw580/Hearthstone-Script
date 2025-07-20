package club.xiaojiawei.hsscript.component

import javafx.scene.control.Label

/**
 * @author 肖嘉威
 * @date 2025/7/2 10:04
 */
open class TagNode(text: String? = null) : Label() {
    init {
        this.text = text
        styleClass.addAll("label-ui", "label-ui-warn", "radius-ui")
        style = "-fx-padding: 0 2 0 2;-fx-font-size:10"
    }
}