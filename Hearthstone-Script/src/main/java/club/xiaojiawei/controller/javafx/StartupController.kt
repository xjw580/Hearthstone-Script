package club.xiaojiawei.controller.javafx

import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.enums.WindowEnum
import club.xiaojiawei.utils.WindowUtil.hideStage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import java.net.URL
import java.util.ResourceBundle
import java.util.Timer
import java.util.TimerTask

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
class StartupController : Initializable {
    @FXML
    private val progressBar: ProgressBar? = null

    @FXML
    private val tip: Text? = null
    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        StartupController.Companion.staticProgressBar = progressBar
        tip!!.setText(ScriptStaticData.SCRIPT_NAME + "启动中......")
        StartupController.Companion.timer = Timer()
        StartupController.Companion.timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (++StartupController.Companion.count == 100) {
                    StartupController.Companion.timer!!.cancel()
                } else {
                    Platform.runLater(Runnable { progressBar!!.setProgress(StartupController.Companion.count.toDouble() / 100) })
                }
            }
        }, 500, 10)
    }

    companion object {
        private var count = 0
        private var staticProgressBar: ProgressBar? = null
        private var timer: Timer? = null

        /**
         * 完成进度条并隐藏此窗口
         */
        @JvmStatic
        fun complete() {
            StartupController.Companion.timer!!.cancel()
            StartupController.Companion.staticProgressBar!!.setProgress(1.0)
            hideStage(WindowEnum.STARTUP)
        }
    }
}
