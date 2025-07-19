package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscript.consts.SCRIPT_NAME
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
class StartupController : Initializable {
    @FXML
    protected lateinit var progressBar: ProgressBar

    @FXML
    protected lateinit var tip: Text

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        staticProgressBar = progressBar
        tip.text = SCRIPT_NAME + "启动中......"
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (++count >= 100) {
                        cancel()
                    } else {
                        Platform.runLater {
                            progressBar.progress =
                                count.toDouble() / 100
                        }
                    }
                }
            }, 0, 20)
        }
    }

    companion object {
        private var count = 0
        private var staticProgressBar: ProgressBar? = null
        private var timer: Timer? = null

        /**
         * 完成进度条并隐藏此窗口
         */
        fun complete() {
            timer?.cancel()
            staticProgressBar?.progress = 1.0
            hideStage(WindowEnum.STARTUP)
        }
    }
}