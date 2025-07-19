package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.NumberField
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil.getString
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import club.xiaojiawei.hsscript.utils.ConfigUtil.store
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Tooltip
import javafx.scene.layout.StackPane
import jdk.internal.misc.VM.saveProperties
import java.net.URL
import java.util.*

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class StrategySettingsController : Initializable {
    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
    }

}