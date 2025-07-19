package club.xiaojiawei.hsscript.controller.javafx.settings

import ch.qos.logback.classic.Level
import club.xiaojiawei.config.DBConfig.DB_NAME
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.controls.Modal
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ProgressModal
import club.xiaojiawei.controls.ico.FileIco
import club.xiaojiawei.hsscript.consts.*
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author 肖嘉威
 * @date 2025/1/20 22:38
 */
class DeveloperSettingsController : Initializable {

    @FXML
    protected lateinit var progressModal: ProgressModal

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var notificationManager: NotificationManager<Node>

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
        SystemUtil.getExeFilePath(HS_CARD_UTIL_FILE)?.let { exeFile ->
            var proxyPath = ""
            if (ConfigUtil.getBoolean(ConfigEnum.USE_PROXY)) {
                NetUtil.getSystemProxy()?.let { proxy ->
                    proxyPath = proxy.address().toString().removePrefix("/")
                }
            }
            go {
                Runtime.getRuntime()
                    .exec(
                        "cmd /c start ${exeFile.absolutePath} --proxyAddress=$proxyPath --dbPath=${
                            Path.of(
                                ROOT_PATH,
                                DB_NAME
                            )
                        }"
                    )
            }
        } ?: let {
            log.error { "找不到${HS_CARD_UTIL_FILE.name}" }
        }
    }

    @FXML
    protected fun createCardAction(actionEvent: ActionEvent) {
        showStage(WindowEnum.CARD_ACTION_EDITOR, null)
    }

    @FXML
    protected fun formatPowerLog() {
        formatLog(GAME_WAR_LOG_NAME) { PowerLogUtil.formatLogFile(it.absolutePath, true) }
    }

    @FXML
    protected fun formatLoadingScreenLog() {
        formatLog(GAME_MODE_LOG_NAME) { LoadingScreenLogUtil.formatLogFile(it.absolutePath, true) }
    }

    private fun formatLog(name: String, exec: (File) -> File?) {
        ScriptStatus.isValidGameInstallPath.isTrue {
            val allLogDir = GameUtil.getAllLogDir()
            val listView = ListView<HBox>().apply {
                styleClass.add("list-view-ui-hs")
                items.addAll(allLogDir.map { f ->
                    HBox(
                        Text(f.name),
                        HBox().apply {
                            HBox.setHgrow(this, Priority.ALWAYS)
                        },
                        Button().apply {
                            styleClass.addAll("btn-ui", "btn-ui-small")
                            graphic = FileIco()
                            contentDisplay = ContentDisplay.RIGHT
                            setOnAction {
                                SystemUtil.openFile(f.absolutePath)
                            }
                        }
                    ).apply {
                        alignment = Pos.CENTER_LEFT
                        padding = Insets(2.0, 0.0, 2.0, 0.0)
                        userData = f
                    }
                })
                sceneProperty().addListener { _, _, newValue ->
                    newValue.stylesheets.add(COMMON_CSS_PATH)
                }
                selectionModel.selectionMode = SelectionMode.MULTIPLE
            }
            val otherLogField = TextField().apply {
                this.styleClass.addAll("text-field-ui", "text-field-ui-small")
            }
            val pane = HBox(
                otherLogField,
                HBox().apply { HBox.setHgrow(this, Priority.ALWAYS) },
                Button("选择其他日志目录").apply {
                    this.styleClass.addAll("btn-ui", "btn-ui-small")
                    setOnAction {
                        val chooser = DirectoryChooser()
                        chooser.title = "选择${GAME_CN_NAME}日志目录"
                        chooser.showDialog(rootPane.scene.window)?.let {
                            otherLogField.text = it.name
                            otherLogField.userData = it
                        }
                    }
                },
            ).apply {
                alignment = Pos.CENTER_LEFT
            }
            val modal = Modal(
                rootPane, "选择要格式化的日志目录",
                VBox(listView, pane).apply {
                    alignment = Pos.TOP_CENTER
                    spacing = 10.0
                },
                {
                    val files =
                        listView.selectionModel.selectedItems.map { it.userData as File }.toMutableList()
                    val otherFile = otherLogField.userData
                    if (otherFile is File) {
                        files.add(otherFile)
                    }
                    if (files.isNotEmpty()) {
                        val progress = progressModal.showByZero("格式化${name}]中")
                        go {
                            val stride = 1.0 / files.size
                            var newFile: File? = null
                            val countDownLatch = CountDownLatch(files.size)
                            for (file in files) {
                                EXTRA_THREAD_POOL.submit {
                                    newFile = exec(file)
                                    countDownLatch.countDown()
                                    runUI {
                                        synchronized(DeveloperSettingsController::class.java) {
                                            progress.set(progress.get() + stride)
                                        }
                                    }
                                }

                            }
                            val res = countDownLatch.await(10, TimeUnit.MINUTES)
                            runUI {
                                progressModal.hide(progress)
                                if (res) {
                                    notificationManager.showSuccess("格式化完成", StackPane(Button("打开").apply {
                                        styleClass.addAll("btn-ui", "btn-ui-small", "btn-ui-normal")
                                        setOnAction {
                                            if (files.size > 1) {
                                                newFile?.let {
                                                    SystemUtil.openFile(it.parentFile.parentFile)
                                                }
                                            } else {
                                                newFile?.let {
                                                    SystemUtil.openFile(it)
                                                }
                                            }
                                        }
                                    }).apply {
                                        style = "-fx-padding:0 0 5 0"
                                    }, 5)
                                } else {
                                    notificationManager.showError("格式化异常", 5)
                                }

                            }
                        }
                    }
                }, {})
            modal.show()
        }.isFalse {
            notificationManager.showError("未配置正确的${GAME_CN_NAME}安装路径", 3)
        }
    }

}
