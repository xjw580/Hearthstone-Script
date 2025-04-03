package club.xiaojiawei.hsscript.controller.javafx

import ch.qos.logback.classic.Level
import club.xiaojiawei.config.DBConfig.DB_NAME
import club.xiaojiawei.config.log
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.consts.HS_CARD_UTIL_FILE
import club.xiaojiawei.hsscript.consts.LOG_PATH
import club.xiaojiawei.hsscript.consts.ROOT_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.StackPane
import java.net.URL
import java.nio.file.Path
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/1/20 22:38
 */
class DeveloperSettingsController : Initializable {

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var notificationManager: NotificationManager<String>

    @FXML
    protected lateinit var fileLogLevelComboBox: ComboBox<String>

    private fun initValue() {
        fileLogLevelComboBox.value = ConfigExUtil.getFileLogLevel().levelStr.uppercase()
    }

    private fun addListener() {
        fileLogLevelComboBox.valueProperty()
            .addListener { observable: ObservableValue<out String>?, oldValue: String?, newValue: String? ->
                newValue?.let {
                    ConfigExUtil.storeFileLogLevel(
                        newValue
                    )
                }
            }
        fileLogLevelComboBox.setCellFactory {
            object : ListCell<String?>() {
                override fun updateItem(s: String?, b: Boolean) {
                    super.updateItem(s, b)
                    if (s == null || b) return
                    val level = Level.valueOf(s)
                    if (level == Level.OFF) {
                        text = level.levelStr
                    } else if (level == Level.ERROR) {
                        if (isSelected) {
                            graphic = Label(level.levelStr)
                        } else {
                            graphic = object : Label(level.levelStr) {
                                init {
                                    style = "-fx-text-fill: #ff0000;"
                                }
                            }
                        }
                    } else if (level == Level.WARN) {
                        if (isSelected) {
                            graphic = Label(level.levelStr)
                        } else {
                            graphic = object : Label(level.levelStr) {
                                init {
                                    style = "-fx-text-fill: #ff8000;"
                                }
                            }
                        }
                    } else if (level == Level.INFO) {
                        graphic = object : Label(level.levelStr) {
                            init {
                                style = "-fx-text-fill: #009e00;"
                            }
                        }
                        if (isSelected) {
                            graphic = Label(level.levelStr)
                        } else {
                            graphic = object : Label(level.levelStr) {
                                init {
                                    style = "-fx-text-fill: #009e00;"
                                }
                            }
                        }
                    } else if (level == Level.DEBUG) {
                        if (isSelected) {
                            graphic = Label(level.levelStr)
                        } else {
                            graphic = object : Label(level.levelStr) {
                                init {
                                    style = "-fx-text-fill: #1982fd;"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        addListener()
    }

    @FXML
    protected fun openLogFile(actionEvent: ActionEvent?) {
        SystemUtil.openFile(LOG_PATH)
    }

    @FXML
    protected fun openMeasureUtil(actionEvent: ActionEvent?) {
//        MeasureApplication.startStage(new Stage());
        showStage(WindowEnum.MEASURE_GAME, null)
    }

    @FXML
    protected fun openGameDataAnalysis(actionEvent: ActionEvent?) {
        showStage(WindowEnum.GAME_DATA_ANALYSIS, null)
    }

    @FXML
    @Suppress("DEPRECATION")
    protected fun updateCardDB(actionEvent: ActionEvent) {
        SystemUtil.getExeFilePath(HS_CARD_UTIL_FILE)?.let { exeFile->
            var proxyPath = ""
            if (ConfigUtil.getBoolean(ConfigEnum.USE_PROXY)){
                NetUtil.getSystemProxy()?.let { proxy ->
                    proxyPath = proxy.address().toString().removePrefix("/")
                }
            }
            go {
                Runtime.getRuntime()
                    .exec("cmd /c start ${exeFile.absolutePath} --proxyAddress=$proxyPath --dbPath=${Path.of(ROOT_PATH, DB_NAME)}")
            }
        } ?: let {
            log.error { "找不到${HS_CARD_UTIL_FILE.name}" }
        }
    }

    @FXML
    protected fun createCardAction(actionEvent: ActionEvent) {
        showStage(WindowEnum.CARD_ACTION_EDITOR, null)
    }

}
