package club.xiaojiawei.hsscript.controller.javafx

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.config.submitExtra
import club.xiaojiawei.controls.CopyLabel
import club.xiaojiawei.controls.Modal
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ico.AbstractIco
import club.xiaojiawei.controls.ico.ClearIco
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum.Companion.fromString
import club.xiaojiawei.hsscript.appender.ExtraLogAppender
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.bean.single.WarEx.resetStatistics
import club.xiaojiawei.hsscript.component.WorkTimeItem
import club.xiaojiawei.hsscript.controller.javafx.view.MainView
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.VersionListener
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.WorkTimeStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil.getString
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import club.xiaojiawei.hsscript.utils.FXUtil
import club.xiaojiawei.hsscript.utils.SystemUtil.copyToClipboard
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.hsscriptbase.util.isFalse
import club.xiaojiawei.hsscriptbase.util.isTrue
import javafx.animation.RotateTransition
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.SetChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.Toggle
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.util.Duration
import javafx.util.StringConverter
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
class MainController : MainView() {
    private var isNotHoverLog = true

    private val runModeMap: MutableMap<RunModeEnum, MutableList<DeckStrategy>> = EnumMap(RunModeEnum::class.java)

    private val workTimeChangeId = "main-ui"

    private var initDate = LocalDate.now()

    override fun initialize(
        url: URL?,
        resourceBundle: ResourceBundle?,
    ) {
        versionText.text = "当前版本：" + VersionListener.currentRelease.tagName
        addListener()
        initModeAndDeck()
        reloadWorkTime()
        go {
            while (true) {
                Thread.sleep(30_000)
                if (LocalDate.now() > initDate) {
                    initDate = LocalDate.now()
                    log.info { "新的一天，应用新的工作时间规则" }
                    reloadWorkTime()
                }
            }
        }
    }

    /**
     * 初始化模式和卡组
     */
    private fun initModeAndDeck() {
        runModeBox.converter =
            object : StringConverter<RunModeEnum?>() {
                override fun toString(runModeEnum: RunModeEnum?): String? = runModeEnum?.comment ?: ""

                override fun fromString(s: String?): RunModeEnum? =
                    if (s == null || s.isBlank()) null else RunModeEnum.valueOf(s)
            }
        deckStrategyBox.converter =
            object : StringConverter<DeckStrategy?>() {
                override fun toString(deckStrategy: DeckStrategy?): String = deckStrategy?.name() ?: ""

                override fun fromString(s: String): DeckStrategy? = null
            }

        reloadRunMode()

        //        模式更改监听
        runModeBox.selectionModel
            .selectedItemProperty()
            .addListener { observable: ObservableValue<out RunModeEnum?>?, oldValue: RunModeEnum?, newValue: RunModeEnum? ->
                val deckStrategies = if (newValue == null) null else runModeMap[newValue]
                deckStrategies?.let {
                    deckStrategyBox.items.setAll(deckStrategies.sortedBy { it.id() })
                } ?: let {
                    deckStrategyBox.items.clear()
                }
                putString(ConfigEnum.DEFAULT_RUN_MODE, newValue?.name ?: "", true)
            }

        //        卡组更改监听
        deckStrategyBox.selectionModel
            .selectedItemProperty()
            .addListener { observable: ObservableValue<out DeckStrategy?>?, oldValue: DeckStrategy?, newValue: DeckStrategy? ->
                if (newValue == null) {
                    runModeMap[runModeBox.selectionModel.selectedItem]?.find { it == oldValue }?.let {
                        deckStrategyBox.selectionModel.select(oldValue)
                        return@addListener
                    }
                } else {
//                将卡组策略的第一个运行模式改为当前运行模式
                    for (i in newValue.runModes.indices) {
                        val runModeEnum = newValue.runModes[i]
                        if (runModeEnum == runModeBox.value) {
                            newValue.runModes[i] = newValue.runModes[0]
                            newValue.runModes[0] = runModeEnum
                            break
                        }
                    }
                }
                DeckStrategyManager.currentDeckStrategy = newValue
            }

        val defaultDeckId = getString(ConfigEnum.DEFAULT_DECK_STRATEGY)
        val defaultRunModeEnum = fromString(getString(ConfigEnum.DEFAULT_RUN_MODE))
        val defaultDeck =
            DeckStrategyManager.deckStrategies
                .stream()
                .filter { deckStrategy: DeckStrategy ->
                    defaultDeckId == deckStrategy.id() &&
                            deckStrategy.runModes.size > 0 &&
                            (
                                    defaultRunModeEnum == null ||
                                            Arrays
                                                .stream(deckStrategy.runModes)
                                                .anyMatch { runModeEnum: RunModeEnum -> runModeEnum == defaultRunModeEnum }
                                    )
                }.findFirst()
        if (defaultDeck.isPresent) {
            val deckStrategy = defaultDeck.get()
            deckStrategy.runModes
            runModeBox.value =
                Objects.requireNonNullElseGet(
                    defaultRunModeEnum,
                ) { deckStrategy.runModes[0] }
            deckStrategyBox.value = deckStrategy
            val deckCode = deckStrategy.deckCode()
            if (!deckCode.isEmpty()) {
                log.info { "当前卡组代码↓" }
                log.info { "$${deckCode}" }
            }
        }

        DeckStrategyManager.currentDeckStrategyProperty.addListener {
                observableValue: ObservableValue<out DeckStrategy?>?,
                deck: DeckStrategy?,
                t1: DeckStrategy?,
            ->
            if (t1 != null) {
                t1.runModes
                runModeBox.value = t1.runModes[0]
                deckStrategyBox.value = t1
            }
        }
    }

    fun reloadRunMode() {
        runModeMap.clear()
        for (deckStrategy in DeckStrategyManager.deckStrategies) {
            for (runModeEnum in deckStrategy.runModes) {
                val strategies = runModeMap.getOrDefault(runModeEnum, ArrayList())
                strategies.add(deckStrategy)
                runModeMap[runModeEnum] = strategies
            }
        }
        runModeBox.items.setAll(runModeMap.keys)
    }

    private fun appendLog(event: ILoggingEvent) {
        runUI {
            val list = logVBox.children
            //                大于二百五条就清空,防止内存泄露和性能问题
            if (list.size > 250) {
                list.clear()
            }
            val label = CopyLabel()
            label.notificationManager = notificationManger
            label.style = "-fx-wrap-text: true"
            label.prefWidthProperty().bind(accordion.widthProperty().subtract(15))

            val levelInt = event.level.levelInt
            var message = event.formattedMessage
            //                处理需要复制的文本
            if (message != null && message.startsWith("$")) {
                message = message.substring(1)
                label.text = message
                label.styleClass.add("copyLog")
                val anchorPane = wrapLabel(label)
                list.add(anchorPane)
                return@runUI
            }
            // 为日志上颜色
            if (event.throwableProxy == null && levelInt <= Level.INFO_INT) {
                label.text = message
            } else if (levelInt <= Level.WARN_INT) {
                label.text = message
                label.styleClass.add("warnLog")
            } else {
                label.text = "$message，查看脚本日志获取详细错误信息"
                label.styleClass.add("errorLog")
            }
            list.add(label)
        }
    }

    private fun wrapLabel(label: Label): AnchorPane {
        val anchorPane = AnchorPane()
        anchorPane.styleClass.add("hoverRootNode")
        val node = FXUtil.buildCopyNode({ copyToClipboard(label.text) })
        node.styleClass.add("hoverChildrenNode")
        anchorPane.children.add(label)
        anchorPane.children.add(node)
        AnchorPane.setRightAnchor(node, 5.0)
        AnchorPane.setTopAnchor(node, 5.0)
        return anchorPane
    }

    private fun addListener() {
        WorkTimeStatus.addWorkTimeSettingListener { list, id ->
            reloadWorkTime(id)
        }
        WorkTimeStatus.addWorkTimeRuleSetListener { list, id ->
            reloadWorkTime(id)
        }
        downloadProgress.progressProperty().addListener { _, _, newValue ->
            val progress = newValue.toDouble()
            val downloading = progress > 0.0 && progress < 1.0
            downloadProgress.isVisible = downloading
            downloadProgress.isManaged = downloading
            downloadProgress.tooltip = Tooltip("下载进度：${String.format("%.1f", progress * 100)}%")
        }
//        日志监听
        Thread({
            while (true) {
                appendLog(ExtraLogAppender.logQueue.take())
            }
        }, "Show Log Thread").start()

        //        暂停状态监听
        PauseStatus.addChangeListener { _, _, t1: Boolean ->
            if (t1) {
                pauseToggleGroup.selectToggle(pauseButton)
            } else {
                pauseToggleGroup.selectToggle(startButton)
            }
        }
//        工作状态监听
        WorkTimeListener.addChangeListener { _, _, t1: Boolean ->
            if (t1) {
                accordion.expandedPane = titledPaneLog
            } else {
                accordion.expandedPane = titledPaneControl
            }
        }

        //        游戏局数监听
        WarEx.warCountProperty.addListener { _, number: Number?, t1: Number ->
            gameCount.text = WarEx.warCount.toString()
            winningPercentage.text = (
                    String.format(
                        "%.1f",
                        WarEx.winCount.toDouble() / WarEx.warCount * 100.0,
                    ) + "%"
                    )
            gameTime.text = formatTime(WarEx.hangingTime)
            exp.text = WarEx.hangingEXP.toString()
        }
        DeckStrategyManager.deckStrategies.addListener(
            SetChangeListener { observable: SetChangeListener.Change<out DeckStrategy?>? ->
                reloadRunMode()
            } as SetChangeListener<in DeckStrategy?>,
        )
        //        是否在下载中监听
        VersionListener.downloadingReadOnlyProperty().addListener { observable, oldValue, newValue ->
            Platform.runLater {
                updateBtn.isDisable = newValue
            }
        }
        //        监听日志自动滑到底部
        logVBox
            .heightProperty()
            .addListener { observable: ObservableValue<out Number>, oldValue: Number, newValue: Number ->
                if (isNotHoverLog) {
                    logScrollPane.vvalue = logScrollPane.vmax
                }
            }
        VersionListener.canUpdateReadOnlyProperty().addListener { observable, oldValue, newValue ->
            Platform.runLater {
                flushBtn.isVisible = !newValue
                flushBtn.isManaged = !newValue
                updateBtn.isVisible = newValue
                updateBtn.isManaged = newValue
            }
        }
        val btnPressedStyleClass = "btnPressed"
        pauseToggleGroup
            .selectedToggleProperty()
            .addListener { _, toggle: Toggle?, t1: Toggle? ->
                if (t1 == null) {
                    if (toggle != null) {
                        pauseToggleGroup.selectToggle(toggle)
                    }
                } else {
                    startButton.styleClass.remove(btnPressedStyleClass)
                    pauseButton.styleClass.remove(btnPressedStyleClass)
                    if (t1 === startButton) {
                        startIco.color = "gray"
                        pauseIco.color = "black"
                        startButton.styleClass.add(btnPressedStyleClass)
                    } else if (t1 === pauseButton) {
                        pauseIco.color = "gray"
                        startIco.color = "black"
                        pauseButton.styleClass.add(btnPressedStyleClass)
                        val graphic = pauseButton.graphic as AbstractIco
                        graphic.color = "gray"
                    }
                }
            }
    }

    private fun createMenuPopup(): Popup {
        val popup = Popup()

        val label = Label("清空")
        label.onMouseClicked =
            EventHandler { event1: MouseEvent? ->
                logVBox.children.clear()
                popup.hide()
            }
        label.style = "-fx-padding: 5 10 5 10"
        label.graphic = ClearIco()
        label.styleClass.addAll("bg-hover-ui", "radius-ui")

        val vBox: VBox =
            object : VBox(label) {
                init {
                    style =
                        "-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3);-fx-padding: 5 3 5 3;-fx-background-color: white"
                }
            }
        vBox.styleClass.add("radius-ui")

        popup.isAutoHide = true
        popup.content.add(vBox)
        return popup
    }

    fun reloadWorkTime(changeId: String? = null) {
        if (changeId == workTimeChangeId) return
        runUI {
            workTimePane.children.clear()
            workTimeRuleSetId.text = ""
            val workTimeRuleSet = WorkTimeStatus.nowWorkTimeRuleSet() ?: return@runUI
            val timeRules = workTimeRuleSet.getTimeRules()
            workTimeRuleSetId.text = workTimeRuleSet.getName()
            for (rule in timeRules) {
                workTimePane.children.add(WorkTimeItem(rule, workTimeChangeId))
            }
        }
    }

    @FXML
    protected fun flushVersion() {
        val transition = RotateTransition(Duration.millis(1200.0), flushIco)
        transition.fromAngle = 0.0
        transition.toAngle = 360.0
        transition.cycleCount = Timeline.INDEFINITE
        transition.play()
        go {
            runCatching {
                VersionListener.checkVersion()
            }
            transition.stop()
        }
    }

    @FXML
    protected fun openSettings() {
        WindowUtil.showStage(WindowEnum.SETTINGS, rootPane.scene.window)
    }

    @FXML
    protected fun updateVersion() {
        if (VersionListener.canUpdate) {
            downloadProgress.progress
            val release = VersionListener.latestRelease ?: return

            VersionListener.downloadLatestRelease(
                false,
                downloadProgress.progressProperty(),
            ) { path: String? ->
                if (path == null) {
                    runUI {
                        WindowUtil
                            .createAlert(
                                String.format("新版本<%s>下载失败", release.tagName),
                                "",
                                rootPane.scene.window,
                            ).show()
                    }
                } else {
                    runUI {
                        WindowUtil
                            .createAlert(
                                "新版本[" + release.tagName + "]下载完毕",
                                "现在更新？",
                                { event: ActionEvent? -> VersionListener.execUpdate(path) },
                                null,
                                rootPane.scene.window,
                            ).show()
                    }
                }
            }
        }
    }

    @FXML
    protected fun resetStatistics(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) return
        val modal =
            Modal(
                rootPane,
                null,
                "重置统计数据？",
                Runnable {
                    Platform.runLater {
                        resetStatistics()
                        gameCount.text = "0"
                        winningPercentage.text = "?"
                        gameTime.text = "0"
                        exp.text = "0"
                        notificationManger.showSuccess("统计数据已重置", 2)
                    }
                },
                Runnable {},
            )
        modal.isMaskClosable = true
        modal.show()
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
        if (event.button == MouseButton.SECONDARY && !logVBox.children.isEmpty()) {
            val menuPopup = createMenuPopup()
            menuPopup.anchorX = event.screenX - 5
            menuPopup.anchorY = event.screenY - 5
            menuPopup.show(rootPane.scene.window)
        }
    }

    @FXML
    protected fun openVersionMsg(mouseEvent: MouseEvent?) {
        WindowUtil.showStage(WindowEnum.VERSION_MSG, rootPane.scene.window)
    }

    @FXML
    protected fun openStatistics(actionEvent: ActionEvent) {
        WindowUtil.showStage(WindowEnum.STATISTICS, rootPane.scene.window)
    }

    @FXML
    protected fun start() {
        submitExtra {
            PauseStatus.setPauseReturn(false).isTrue {
                runUI {
                    pauseToggleGroup.selectToggle(pauseButton)
                }
            }
        }
    }

    @FXML
    protected fun pause() {
        submitExtra {
            PauseStatus.setPauseReturn(true).isFalse {
                runUI {
                    pauseToggleGroup.selectToggle(startButton)
                }
            }
        }
    }

    @FXML
    protected fun editWorkTime() {
        WindowUtil.showStage(WindowEnum.TIME_SETTINGS)
    }

    companion object {
        private fun formatTime(time: Int): String {
            val timeStr =
                if (time == 0) {
                    String.format("%d", time)
                } else if (time < 60) {
                    String.format("%dm", time)
                } else if (time < 1440) {
                    if (time % 60 == 0) {
                        String.format("%dh", time / 60)
                    } else {
                        String.format("%dh%dm", time / 60, time % 60)
                    }
                } else {
                    if (time % 1440 == 0) {
                        String.format("%dd", time / 1440)
                    } else {
                        String.format("%dd%dh", time / 1440, time % 1440 / 60)
                    }
                }
            return timeStr
        }
    }

    fun getNotificationManagerInstance(): NotificationManager<Any> = notificationManger
}
