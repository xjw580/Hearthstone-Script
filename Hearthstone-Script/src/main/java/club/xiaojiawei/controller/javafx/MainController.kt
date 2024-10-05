package club.xiaojiawei.controller.javafx

import club.xiaojiawei.controls.Time
import club.xiaojiawei.utils.PropertiesUtil
import jakarta.annotation.Resource
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import javafx.scene.text.Text
import javafx.stage.Popup
import javafx.util.Duration
import javafx.util.StringConverter
import lombok.Getter
import org.springframework.stereotype.Component
import java.io.File
import java.lang.String
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.util.ArrayList
import java.util.Optional
import java.util.Properties
import java.util.function.Consumer

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
@Component
@Slf4j
class MainController : Initializable {
    @FXML
    private val startIco: StartIco? = null

    @FXML
    private val pauseIco: PauseIco? = null

    @FXML
    private val pauseToggleGroup: ToggleGroup? = null

    @FXML
    private val rootPane: StackPane? = null

    @FXML
    @Getter
    private val notificationManger: NotificationManager<Any?>? = null

    @FXML
    private val logScrollPane: ScrollPane? = null

    @FXML
    private val updateBtn: Button? = null

    @FXML
    private val flushBtn: Button? = null

    @FXML
    private val flushIco: FlushIco? = null

    @FXML
    private val versionText: Text? = null

    @FXML
    private val logVBox: VBox? = null

    @FXML
    private val accordion: Accordion? = null

    @FXML
    private val startButton: ToggleButton? = null

    @FXML
    private val pauseButton: ToggleButton? = null

    @FXML
    private val titledPaneLog: TitledPane? = null

    @FXML
    @Getter
    private val gameCount: Text? = null

    @FXML
    @Getter
    private val winningPercentage: Text? = null

    @FXML
    @Getter
    private val gameTime: Text? = null

    @FXML
    @Getter
    private val exp: Text? = null

    @FXML
    private val runModeBox: ComboBox<RunModeEnum?>? = null

    @FXML
    private val deckBox: ComboBox<DeckStrategy?>? = null

    @FXML
    private val workDay: TilePane? = null

    @FXML
    private val workTime: VBox? = null

    @FXML
    private val downloadProgress: ProgressBar? = null

    @Resource
    private val isPause: AtomicReference<BooleanProperty?>? = null

    @Resource
    private val propertiesUtil: PropertiesUtil? = null

    @Resource
    private val scriptConfiguration: Properties? = null

    @Resource
    private val versionListener: VersionListener? = null
    private var isNotHoverLog = true

    @FXML
    private val titledPaneControl: TitledPane? = null

    fun expandedLogPane() {
        accordion.setExpandedPane(titledPaneLog)
    }

    private fun assign() {
        MainController.Companion.staticLogVBox = logVBox
        MainController.Companion.staticAccordion = accordion
        MainController.Companion.staticDownloadProgress = downloadProgress
        MainController.Companion.staticIsPause = isPause
        MainController.Companion.staticNotificationManger = notificationManger
    }

    private val runModeMap: MutableMap<RunModeEnum?, MutableList<DeckStrategy?>> =
        HashMap<RunModeEnum?, MutableList<DeckStrategy?>>()

    /**
     * 初始化模式和卡组
     */
    private fun initModeAndDeck() {
        runModeBox.setConverter(object : StringConverter<RunModeEnum?>() {
            override fun toString(runModeEnum: RunModeEnum?): String {
                return if (runModeEnum == null) "" else runModeEnum.comment
            }

            override fun fromString(s: String?): RunModeEnum? {
                return if ((s == null || s.isBlank())) null else RunModeEnum.valueOf(s)
            }
        })
        deckBox.setConverter(object : StringConverter<DeckStrategy?>() {
            override fun toString(deckStrategy: DeckStrategy?): String {
                return if (deckStrategy == null) "" else deckStrategy.name()
            }

            override fun fromString(s: String?): DeckStrategy? {
                return null
            }
        })

        reloadRunMode()

        //        模式更改监听
        runModeBox.getSelectionModel().selectedItemProperty()
            .addListener(ChangeListener { observable: ObservableValue<out RunModeEnum?>?, oldValue: RunModeEnum?, newValue: RunModeEnum? ->
                deckBox.getSelectionModel().select(null)
                if (newValue == null) {
                    deckBox.getItems().clear()
                } else {
                    deckBox.getItems().setAll(runModeMap.get(newValue))
                }
            })

        //        卡组更改监听
        deckBox.getSelectionModel().selectedItemProperty()
            .addListener(ChangeListener { observable: ObservableValue<out DeckStrategy?>?, oldValue: DeckStrategy?, newValue: DeckStrategy? ->
                if (newValue != null) {
//                将卡组策略的第一个运行模式改为当前运行模式
                    for (i in newValue.runModes.indices) {
                        val runModeEnum: RunModeEnum = newValue.runModes[i]
                        if (runModeEnum == runModeBox.getValue()) {
                            newValue.runModes[i] = newValue.runModes[0]
                            newValue.runModes[0] = runModeEnum
                            break
                        }
                    }
                }
                DeckStrategyManager.CURRENT_DECK_STRATEGY.set(newValue)
            })

        val defaultDeck: Optional<DeckStrategy?> = DeckStrategyManager.DECK_STRATEGIES.stream()
            .filter { deckStrategy: DeckStrategy? -> scriptConfiguration!!.getProperty(ConfigEnum.DEFAULT_DECK_STRATEGY.getKey()) == deckStrategy.id() }
            .findFirst()
        if (defaultDeck.isPresent()) {
            defaultDeck.get().runModes
            runModeBox.setValue(defaultDeck.get().runModes[0])
            deckBox.setValue(defaultDeck.get())
        }

        DeckStrategyManager.CURRENT_DECK_STRATEGY.addListener(ChangeListener { observableValue: ObservableValue<out DeckStrategy?>?, deck: DeckStrategy?, t1: DeckStrategy? ->
            if (t1 != null) {
                t1.runModes
                runModeBox.setValue(t1.runModes[0])
                deckBox.setValue(t1)
            }
        })
    }

    fun reloadRunMode() {
        runModeMap.clear()
        for (deckStrategy in DeckStrategyManager.DECK_STRATEGIES) {
            for (runModeEnum in deckStrategy.runModes) {
                val strategies: MutableList<DeckStrategy?> =
                    runModeMap.getOrDefault(runModeEnum, ArrayList<DeckStrategy?>())
                strategies.add(deckStrategy)
                runModeMap.put(runModeEnum, strategies)
            }
        }
        runModeBox.getItems().setAll(runModeMap.keys)
    }


    private fun addListener() {
        DeckStrategyManager.DECK_STRATEGIES.addListener(SetChangeListener { observable: SetChangeListener.Change<out DeckStrategy?>? ->
            reloadRunMode()
        } as SetChangeListener<in DeckStrategy?>)
        //        是否在更新中监听
        MainController.Companion.UPDATING.addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
            updateBtn!!.setDisable(
                newValue!!
            )
        })
        //        监听日志自动滑到底部
        logVBox.heightProperty()
            .addListener(ChangeListener { observable: ObservableValue<out kotlin.Number?>?, oldValue: Number?, newValue: Number? ->
                if (isNotHoverLog) {
                    logScrollPane!!.setVvalue(logScrollPane.getVmax())
                }
            })
        VersionListener.canUpdateReadOnlyProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                flushBtn!!.setVisible(!newValue!!)
                flushBtn.setManaged(!newValue)
                updateBtn!!.setVisible(newValue)
                updateBtn.setManaged(newValue)
            })
        val btnPressedStyleClass = "btnPressed"
        pauseToggleGroup.selectedToggleProperty()
            .addListener(ChangeListener { observableValue: ObservableValue<out Toggle?>?, toggle: Toggle?, t1: Toggle? ->
                if (t1 == null) {
                    if (toggle != null) {
                        pauseToggleGroup.selectToggle(toggle)
                    }
                } else {
                    startButton.getStyleClass().remove(btnPressedStyleClass)
                    pauseButton.getStyleClass().remove(btnPressedStyleClass)
                    if (t1 === startButton) {
                        startIco.setColor("gray")
                        pauseIco.setColor("black")
                        startButton.getStyleClass().add(btnPressedStyleClass)
                        isPause.get().set(false)
                    } else if (t1 === pauseButton) {
                        pauseIco.setColor("gray")
                        startIco.setColor("black")
                        pauseButton.getStyleClass().add(btnPressedStyleClass)
                        val graphic: AbstractIco = pauseButton.getGraphic() as AbstractIco
                        graphic.setColor("gray")
                        isPause.get().set(true)
                    }
                }
            })
    }

    private fun createMenuPopup(): Popup {
        val popup = Popup()

        val label = Label("清空")
        label.setOnMouseClicked(EventHandler { event1: MouseEvent? ->
            logVBox.getChildren().clear()
            popup.hide()
        })
        label.setStyle("-fx-padding: 5 10 5 10")
        label.setGraphic(ClearIco())
        label.getStyleClass().addAll("bg-hover-ui", "radius-ui")

        val vBox: VBox = object : VBox(label) {
            init {
                setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3);-fx-padding: 5 3 5 3;-fx-background-color: white")
            }
        }
        vBox.getStyleClass().add("radius-ui")

        popup.setAutoHide(true)
        popup.getContent().add(vBox)
        return popup
    }

    /**
     * 初始化挂机时间
     */
    fun initWorkDate() {
//        初始化挂机天
        val workDayFlagArr: Array<String?> = Work.getWorkDayFlagArr()
        val workDayChildren: ObservableList<Node?> = workDay.getChildren()
        for (i in workDayFlagArr.indices) {
            (workDayChildren.get(i) as CheckBox).setSelected(workDayFlagArr[i] == "true")
        }
        //        初始化挂机段
        val workTimeFlagArr: Array<String?> = Work.getWorkTimeFlagArr()
        val workTimeArr: Array<String?> = Work.getWorkTimeArr()
        val workTimeChildren: ObservableList<Node?> = workTime.getChildren()
        for (i in workTimeFlagArr.indices) {
            val timeHBox: HBox = workTimeChildren.get(i) as HBox
            val timeControls: ObservableList<Node?> = (timeHBox.getChildren().get(1) as HBox).getChildren()
            if (workTimeArr[i] != null && workTimeArr[i] != "null" && !workTimeArr[i]!!.isBlank()) {
                val times: Array<String?> =
                    workTimeArr[i]!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                (timeControls.get(0) as Time).setTime(times[0])
                (timeControls.get(2) as Time).setTime(times[1])
                (timeHBox.getChildren().get(0) as CheckBox).setSelected(workTimeFlagArr[i] == "true")
            } else {
                (timeControls.get(0) as Time).setTime(null)
                (timeControls.get(2) as Time).setTime(null)
                (timeHBox.getChildren().get(0) as CheckBox).setSelected(false)
            }
        }
    }

    fun changeSwitch(isPause: Boolean) {
        if (isPause) {
            pauseToggleGroup.selectToggle(pauseButton)
        } else {
            pauseToggleGroup.selectToggle(startButton)
        }
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        versionText!!.setText("当前版本：" + VersionListener.getCurrentRelease().getTagName())
        assign()
        initModeAndDeck()
        initWorkDate()
        addListener()
    }

    @FXML
    protected fun flushVersion() {
        val transition: RotateTransition = RotateTransition(Duration.millis(1200.0), flushIco)
        transition.setFromAngle(0.0)
        transition.setToAngle(360.0)
        transition.setCycleCount(Timeline.INDEFINITE)
        try {
            transition.play()
            versionListener.checkVersion()
            if (VersionListener.isCanUpdate()) {
                notificationManger.showSuccess("发现新版本", 2)
            } else {
                notificationManger.showInfo("已是最新版本", 2)
            }
        } finally {
            transition.stop()
        }
    }

    @FXML
    protected fun openSettings() {
        val stage: Stage = WindowUtil.buildStage(WindowEnum.SETTINGS)
        if (stage.getOwner() == null) {
            stage.initOwner(WindowUtil.getStage(WindowEnum.MAIN))
        }
        stage.show()
    }

    @FXML
    protected fun updateVersion() {
        val release: Release? = VersionListener.getLatestRelease()
        if (release != null) {
            MainController.Companion.downloadRelease(release, false, Consumer? { path: String? ->
                if (path == null) {
                    Platform.runLater(Runnable {
                        WindowUtil.createAlert(
                            String.format(
                                "新版本<%s>下载失败",
                                release.getTagName()
                            ), "", rootPane.getScene().getWindow()
                        ).show()
                    })
                } else {
                    Platform.runLater(Runnable {
                        WindowUtil.createAlert(
                            "新版本[" + release.getTagName() + "]下载完毕",
                            "现在更新？",
                            EventHandler { event: ActionEvent? -> execUpdate(path) },
                            EventHandler { event: ActionEvent? -> MainController.Companion.UPDATING.set(false) },
                            rootPane.getScene().getWindow()
                        ).show()
                    })
                }
            })
        }
    }

    @FXML
    protected fun saveTime() {
//        检查挂机天
        val workDayChildren: ObservableList<Node?> = workDay.getChildren()
        val workDayFlagArr: Array<String?> = Work.getWorkDayFlagArr()
        for (i in workDayChildren.indices) {
            workDayFlagArr[i] = (workDayChildren.get(i) as CheckBox).isSelected().toString()
        }
        //        检查挂机段
        val workTimeChildren: ObservableList<Node?> = workTime.getChildren()
        val workTimeFlagArr: Array<String?> = Work.getWorkTimeFlagArr()
        val workTimeArr: Array<String?> = Work.getWorkTimeArr()
        for (i in workTimeChildren.indices) {
            val hBox: HBox = workTimeChildren.get(i) as HBox
            val children: ObservableList<Node?> = (hBox.getChildren().get(1) as HBox).getChildren()
            val startTime = children.get(0) as Time
            val endTime = children.get(2) as Time
            val timeCheckBox: CheckBox = hBox.getChildren().get(0) as CheckBox
            if (startTime.timeProperty().get() != null && endTime.timeProperty().get() != null) {
                workTimeArr[i] = String.join("-", startTime.getTime(), endTime.getTime())
                workTimeFlagArr[i] = timeCheckBox.isSelected().toString()
            } else {
                workTimeArr[i] = "null"
                startTime.setTime(null)
                endTime.setTime(null)
                workTimeFlagArr[i] = false.toString()
                timeCheckBox.setSelected(false)
            }
            startTime.refresh()
            endTime.refresh()
        }
        Work.storeWorkDate()
        notificationManger.showSuccess("工作时间保存成功", 2)
    }

    @FXML
    protected fun mouseEnteredLog() {
        isNotHoverLog = false
    }

    @FXML
    protected fun mouseExitedLog() {
        isNotHoverLog = true
    }

    @FXML
    protected fun mouseClickedLog(event: MouseEvent) {
        if (event.getButton() == MouseButton.SECONDARY && !logVBox.getChildren().isEmpty()) {
            val menuPopup = createMenuPopup()
            menuPopup.setAnchorX(event.getScreenX() - 5)
            menuPopup.setAnchorY(event.getScreenY() - 5)
            menuPopup.show(rootPane.getScene().getWindow())
        }
    }

    companion object {
        @Getter
        private var staticLogVBox: VBox? = null

        @Getter
        private var staticAccordion: Accordion? = null

        @Getter
        private var staticNotificationManger: NotificationManager<Any?>? = null
        private var staticDownloadProgress: ProgressBar? = null
        private var staticIsPause: AtomicReference<BooleanProperty?>? = null
        private val UPDATING: SimpleBooleanProperty = SimpleBooleanProperty(false)
        private const val VERSION_FILE_FLAG_NAME = "downloaded.flag"
        fun downloadRelease(release: Release, force: Boolean, callback: Consumer<kotlin.String?>?) {
            if (MainController.Companion.UPDATING.get()) {
                return
            }
            MainController.Companion.UPDATING.set(true)
            EXTRA_THREAD_POOL.submit(Runnable {
                var path: kotlin.String? = null
                try {
                    val file = Path.of(
                        ScriptStaticData.TEMP_VERSION_PATH,
                        release.getTagName(),
                        MainController.Companion.VERSION_FILE_FLAG_NAME
                    ).toFile()
                    if (!force && file.exists()) {
                        path = file.getParentFile().getAbsolutePath()
                    } else if ((MainController.Companion.downloadRelease(
                            release,
                            GiteeRepository.getInstance().getReleaseURL(release)
                        ).also { path = it }) == null
                    ) {
                        Platform.runLater(Runnable {
                            MainController.Companion.staticNotificationManger.showInfo(
                                "更换下载源重新下载",
                                3
                            )
                        })
                        path = MainController.Companion.downloadRelease(
                            release,
                            GithubRepository.getInstance().getReleaseURL(release)
                        )
                    }
                } finally {
                    MainController.Companion.UPDATING.set(false)
                    if (callback != null) {
                        callback.accept(path)
                    }
                }
            })
        }

        private fun downloadRelease(release: Release, url: kotlin.String): kotlin.String? {
            var rootPath: Path?
            try {
                URI(url)
                    .toURL()
                    .openConnection()
                    .getInputStream().use { inputStream ->
                        ZipInputStream(inputStream).use { zipInputStream ->
                            val startContent = "开始下载<" + release.getTagName() + ">"
                            MainController.log.info(startContent)
                            Platform.runLater(Runnable {
                                MainController.Companion.staticNotificationManger.showInfo(
                                    startContent,
                                    2
                                )
                            })
                            MainController.Companion.staticDownloadProgress.setProgress(0.0)
                            MainController.Companion.staticDownloadProgress.setVisible(true)
                            MainController.Companion.staticDownloadProgress.setManaged(true)
                            var nextEntry: ZipEntry?
                            var index = 0.0
                            val count = 74.0
                            rootPath = Path.of(ScriptStaticData.TEMP_VERSION_PATH, release.getTagName())
                            val rootFile = rootPath.toFile()
                            if (!rootFile.exists() && !rootFile.mkdirs()) {
                                MainController.log.error(rootFile.getAbsolutePath() + "创建失败")
                                return null
                            }
                            while ((zipInputStream.getNextEntry().also { nextEntry = it }) != null) {
                                val entryFile: File = rootPath.resolve(nextEntry.getName()).toFile()
                                if (nextEntry.isDirectory()) {
                                    if (entryFile.mkdirs()) {
                                        MainController.log.info("created_dir：" + entryFile.getPath())
                                    }
                                } else {
                                    val parentFile = entryFile.getParentFile()
                                    if (parentFile.exists() || parentFile.mkdirs()) {
                                        BufferedOutputStream(FileOutputStream(entryFile)).use { bufferedOutputStream ->
                                            var l: Int
                                            val bytes = ByteArray(8192)
                                            while ((zipInputStream.read(bytes).also { l = it }) != -1) {
                                                bufferedOutputStream.write(bytes, 0, l)
                                            }
                                        }
                                        MainController.log.info("downloaded_file：" + entryFile.getPath())
                                    }
                                }
                                MainController.Companion.staticDownloadProgress.setProgress(++index / count)
                            }
                            MainController.Companion.writeVersionFileCompleteFlag(rootPath.toString())
                            MainController.Companion.staticDownloadProgress.setProgress(1.0)
                            val endContent = "<" + release.getTagName() + ">下载完毕"
                            MainController.log.info(endContent)
                            Platform.runLater(Runnable {
                                MainController.Companion.staticNotificationManger.showSuccess(
                                    endContent,
                                    2
                                )
                            })
                        }
                    }
            } catch (e: IOException) {
                val errorContent = "<" + release.getTagName() + ">下载失败"
                MainController.log.error(errorContent + "," + url, e)
                Platform.runLater(Runnable {
                    MainController.Companion.staticNotificationManger.showError(
                        errorContent,
                        2
                    )
                })
                return null
            } catch (e: URISyntaxException) {
                val errorContent = "<" + release.getTagName() + ">下载失败"
                MainController.log.error(errorContent + "," + url, e)
                Platform.runLater(Runnable {
                    MainController.Companion.staticNotificationManger.showError(
                        errorContent,
                        2
                    )
                })
                return null
            } finally {
                MainController.Companion.staticDownloadProgress.setVisible(false)
                MainController.Companion.staticDownloadProgress.setManaged(false)
            }
            return rootPath.toString()
        }

        private fun writeVersionFileCompleteFlag(path: kotlin.String): Boolean {
            try {
                return Path.of(path, MainController.Companion.VERSION_FILE_FLAG_NAME).toFile().createNewFile()
            } catch (e: IOException) {
                MainController.log.error("", e)
            }
            return false
        }

        @JvmStatic
        fun execUpdate(versionPath: kotlin.String?) {
            try {
                MainController.Companion.UPDATING.set(true)
                val rootPath = System.getProperty("user.dir")
                val updateProgramPath = rootPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME
                Files.copy(
                    Path.of(versionPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME),
                    Path.of(rootPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME),
                    StandardCopyOption.REPLACE_EXISTING
                )
                Runtime.getRuntime().exec(
                    kotlin.String.format(
                        "%s --target='%s' --source='%s' --pause='%s' --pid='%s'",
                        updateProgramPath,
                        rootPath,
                        versionPath,
                        MainController.Companion.staticIsPause.get().get(),
                        ProcessHandle.current().pid()
                    )
                )
            } catch (e: IOException) {
                MainController.log.error("执行版本更新失败", e)
            } finally {
                MainController.Companion.UPDATING.set(false)
            }
        }
    }
}