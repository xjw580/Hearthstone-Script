package club.xiaojiawei.hsscript.controller.javafx

import ch.qos.logback.classic.Level
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.data.LOG_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getFileLogLevel
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeFileLogLevel
import club.xiaojiawei.hsscript.utils.ConfigUtil.getBoolean
import club.xiaojiawei.hsscript.utils.ConfigUtil.putBoolean
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/1/20 22:38
 */
class DeveloperSettingsController : Initializable {

    @FXML
    lateinit var autoOpenAnalysis: Switch

    @FXML
    lateinit var notificationManager: NotificationManager<String>

    @FXML
    lateinit var fileLogLevelComboBox: ComboBox<String>

    @FXML
    lateinit var strategySwitch: Switch

    private fun initValue() {
        strategySwitch.status = getBoolean(ConfigEnum.STRATEGY)
        fileLogLevelComboBox.value = getFileLogLevel().levelStr.uppercase()
        autoOpenAnalysis.status = getBoolean(ConfigEnum.AUTO_OPEN_GAME_ANALYSIS)
    }

    private fun addListener() {
        //        监听策略开关
        strategySwitch.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.STRATEGY,
                    newValue, true
                )
            }
        autoOpenAnalysis.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.AUTO_OPEN_GAME_ANALYSIS,
                    newValue, true
                )
            }
        fileLogLevelComboBox.valueProperty()
            .addListener { observable: ObservableValue<out String>?, oldValue: String?, newValue: String? ->
                newValue?.let {
                    storeFileLogLevel(
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
        SystemUtil.openPath(LOG_PATH)
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
}
