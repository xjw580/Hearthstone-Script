package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TitledPane
import javafx.scene.layout.StackPane

/**
 * @author 肖嘉威
 * @date 2025/2/6 12:52
 */
class MeasureGameController {

    @FXML
    lateinit var codeStyleSwitch: Switch

    @FXML
    lateinit var usePane: TitledPane

    @FXML
    lateinit var notificationManager: NotificationManager<String>

    @FXML
    lateinit var rootPane: StackPane

    @FXML
    fun showGameModal(actionEvent: ActionEvent) {
    }
}
