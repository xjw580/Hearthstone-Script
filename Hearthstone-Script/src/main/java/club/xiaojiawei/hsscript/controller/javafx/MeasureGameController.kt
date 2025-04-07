package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.layout.StackPane

/**
 * @author 肖嘉威
 * @date 2025/2/6 12:52
 */
class MeasureGameController {

    @FXML
    protected lateinit var resultArea: TextArea

    @FXML
    protected lateinit var ktStyleSwitch: Switch

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected fun showGameModal(actionEvent: ActionEvent) {
        val source = actionEvent.source
        source as ToggleButton
        if (source.isSelected) {
            WindowUtil.showStage(WindowEnum.GAME_WINDOW_MODAL, rootPane.scene.window)
            val controller = WindowUtil.getController(WindowEnum.GAME_WINDOW_MODAL)
            if (controller is GameWindowModalController){
                controller.setOpacity(0.2)
            }
        } else {
            WindowUtil.hideStage(WindowEnum.GAME_WINDOW_MODAL)
        }
    }

    @FXML
    protected fun printResult(actionEvent: ActionEvent) {
        WindowUtil.getController(WindowEnum.GAME_WINDOW_MODAL)?.let {
            it as GameWindowModalController
            val builder = StringBuilder()
            for ((index, gameRect) in it.getGameRect().withIndex()) {
                if (ktStyleSwitch.status) {
                    builder.append(
                        String.format(
                            "val RECT%d = GameRect(%.4f, %.4f, %.4f, %.4f)\n",
                            index + 1, gameRect.left, gameRect.right, gameRect.top, gameRect.bottom
                        )
                    )
                } else {
                    builder.append(
                        String.format(
                            "public static final GameRect RECT%d = new GameRect(%.4fD, %.4fD, %.4fD, %.4fD);\n",
                            index + 1, gameRect.left, gameRect.right, gameRect.top, gameRect.bottom
                        )
                    )
                }
            }
            val result = builder.toString()
            if (result.isNotEmpty()) {
                SystemUtil.copyToClipboard(result)
                resultArea.appendText(result)
                notificationManager.showSuccess("已复制到剪切板", 1)
            }
        }
    }

    @FXML
    protected fun clearResult(actionEvent: ActionEvent) {
        resultArea.text = ""
    }

}