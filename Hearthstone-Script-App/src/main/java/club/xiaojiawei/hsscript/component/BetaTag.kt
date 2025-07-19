package club.xiaojiawei.hsscript.component

import javafx.scene.control.Tooltip

/**
 * @author 肖嘉威
 * @date 2025/7/2 10:04
 */
class BetaTag() : TagNode("Beta") {
    init {
        tooltip = Tooltip("此项功能处于测试阶段")
    }
}