package club.xiaojiawei.hsscript.controller.javafx

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.controls.*
import club.xiaojiawei.controls.ico.AbstractIco
import club.xiaojiawei.controls.ico.ClearIco
import club.xiaojiawei.controls.ico.CopyIco
import club.xiaojiawei.controls.ico.OKIco
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.enums.RunModeEnum.Companion.fromString
import club.xiaojiawei.hsscript.appender.ExtraLogAppender
import club.xiaojiawei.hsscript.bean.WorkDay
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.bean.single.WarEx.resetStatistics
import club.xiaojiawei.hsscript.controller.javafx.view.MainView
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.VersionListener.canUpdate
import club.xiaojiawei.hsscript.listener.VersionListener.canUpdateReadOnlyProperty
import club.xiaojiawei.hsscript.listener.VersionListener.checkVersion
import club.xiaojiawei.hsscript.listener.VersionListener.currentRelease
import club.xiaojiawei.hsscript.listener.VersionListener.downloadLatestRelease
import club.xiaojiawei.hsscript.listener.VersionListener.downloadingReadOnlyProperty
import club.xiaojiawei.hsscript.listener.VersionListener.execUpdate
import club.xiaojiawei.hsscript.listener.VersionListener.latestRelease
import club.xiaojiawei.hsscript.status.DeckStrategyManager.currentDeckStrategy
import club.xiaojiawei.hsscript.status.DeckStrategyManager.currentDeckStrategyProperty
import club.xiaojiawei.hsscript.status.DeckStrategyManager.deckStrategies
import club.xiaojiawei.hsscript.status.PauseStatus.addListener
import club.xiaojiawei.hsscript.status.PauseStatus.asyncSetPause
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getWorkDay
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getWorkTime
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeWorkDay
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeWorkTime
import club.xiaojiawei.hsscript.utils.ConfigUtil.getString
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import club.xiaojiawei.hsscript.utils.SystemUtil.copyToClipboard
import club.xiaojiawei.hsscript.utils.WindowUtil.buildStage
import club.xiaojiawei.hsscript.utils.WindowUtil.createAlert
import club.xiaojiawei.hsscript.utils.WindowUtil.getStage
import club.xiaojiawei.hsscript.utils.WindowUtil.showStage
import javafx.animation.PauseTransition
import javafx.animation.RotateTransition
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.collections.SetChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Popup
import javafx.util.Duration
import javafx.util.StringConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import java.util.stream.Collectors
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
class MainController : MainView() {

    private var isNotHoverLog = true

    private val runModeMap: MutableMap<RunModeEnum, MutableList<DeckStrategy>> = EnumMap(RunModeEnum::class.java)

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        versionText.text = "当前版本：" + currentRelease.tagName
        addListener()
        initModeAndDeck()
        initWorkDate()
    }

    /**
     * 初始化模式和卡组
     */
    private fun initModeAndDeck() {
        runModeBox.converter = object : StringConverter<RunModeEnum?>() {
            override fun toString(runModeEnum: RunModeEnum?): String {
                return runModeEnum?.comment ?: ""
            }

            override fun fromString(s: String): RunModeEnum? {
                return if (s == null || s.isBlank()) null else RunModeEnum.valueOf(s)
            }
        }
        deckBox.converter = object : StringConverter<DeckStrategy?>() {
            override fun toString(deckStrategy: DeckStrategy?): String {
                return deckStrategy?.name() ?: ""
            }

            override fun fromString(s: String): DeckStrategy? {
                return null
            }
        }

        reloadRunMode()

        //        模式更改监听
        runModeBox.selectionModel.selectedItemProperty()
            .addListener { observable: ObservableValue<out RunModeEnum?>?, oldValue: RunModeEnum?, newValue: RunModeEnum? ->
                deckBox.selectionModel.select(null)
                if (newValue == null) {
                    deckBox.items.clear()
                } else {
                    deckBox.items.setAll(runModeMap[newValue])
                }
                putString(ConfigEnum.DEFAULT_RUN_MODE, newValue?.name ?: "", true)
            }

        //        卡组更改监听
        deckBox.selectionModel.selectedItemProperty()
            .addListener { observable: ObservableValue<out DeckStrategy?>?, oldValue: DeckStrategy?, newValue: DeckStrategy? ->
                if (newValue != null) {
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
                currentDeckStrategy = newValue
            }

        val defaultDeckId = getString(ConfigEnum.DEFAULT_DECK_STRATEGY)
        val defaultRunModeEnum = fromString(getString(ConfigEnum.DEFAULT_RUN_MODE))
        val defaultDeck = deckStrategies
            .stream()
            .filter { deckStrategy: DeckStrategy ->
                defaultDeckId == deckStrategy.id()
                        && deckStrategy.runModes.size > 0 &&
                        (defaultRunModeEnum == null
                                ||
                                Arrays.stream(deckStrategy.runModes)
                                    .anyMatch { runModeEnum: RunModeEnum -> runModeEnum == defaultRunModeEnum })
            }
            .findFirst()
        if (defaultDeck.isPresent) {
            val deckStrategy = defaultDeck.get()
            deckStrategy.runModes
            runModeBox.value = Objects.requireNonNullElseGet(
                defaultRunModeEnum
            ) { deckStrategy.runModes[0] }
            deckBox.value = deckStrategy
            val deckCode = deckStrategy.deckCode()
            if (!deckCode.isEmpty()) {
                log.info("当前卡组代码↓")
                log.info("\${}", deckCode)
            }
        }

        currentDeckStrategyProperty.addListener { observableValue: ObservableValue<out DeckStrategy?>?, deck: DeckStrategy?, t1: DeckStrategy? ->
            if (t1 != null) {
                t1.runModes
                runModeBox.value = t1.runModes[0]
                deckBox.value = t1
            }
        }
    }

    fun reloadRunMode() {
        runModeMap.clear()
        for (deckStrategy in deckStrategies) {
            for (runModeEnum in deckStrategy.runModes) {
                val strategies = runModeMap.getOrDefault(runModeEnum, ArrayList())
                strategies.add(deckStrategy)
                runModeMap[runModeEnum] = strategies
            }
        }
        runModeBox.items.setAll(runModeMap.keys)
    }

    private fun appendLog(event: ILoggingEvent) {
        Platform.runLater {
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
                return@runLater
            }
            /*为日志上颜色*/
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
        val node = buildCopyNode { copyToClipboard(label.text) }
        node.styleClass.add("hoverChildrenNode")
        anchorPane.children.add(label)
        anchorPane.children.add(node)
        AnchorPane.setRightAnchor(node, 5.0)
        AnchorPane.setTopAnchor(node, 5.0)
        return anchorPane
    }

    private fun buildCopyNode(clickHandler: Runnable?): Node {
        val graphicLabel = Label()
        val copyIco = CopyIco()
        val icoColor = "#e4e4e4"
        copyIco.color = icoColor
        graphicLabel.graphic = copyIco
        graphicLabel.style = """
                    -fx-cursor: hand;
                    -fx-alignment: CENTER;
                    -fx-pref-width: 22;
                    -fx-pref-height: 22;
                    -fx-background-radius: 3;
                    -fx-background-color: rgba(128,128,128,0.9);
                    -fx-font-size: 10;
                    
                    """.trimIndent()
        graphicLabel.onMouseClicked = EventHandler<MouseEvent> { actionEvent: MouseEvent? ->
            clickHandler?.run()
            val okIco = OKIco()
            okIco.color = icoColor
            graphicLabel.graphic = okIco
            val pauseTransition = PauseTransition(Duration.millis(1000.0))
            pauseTransition.onFinished = EventHandler { actionEvent1: ActionEvent? ->
                graphicLabel.graphic = copyIco
            }
            pauseTransition.play()
        }
        return graphicLabel
    }


    private fun addListener() {
//        日志监听
        ExtraLogAppender.addCallback { event: ILoggingEvent -> this.appendLog(event) }

        //        暂停状态监听
        addListener { observableValue: ObservableValue<out Boolean>?, aBoolean: Boolean?, t1: Boolean ->
            changeSwitch(
                t1
            )
        }

        val warInstance = WarEx

        //        游戏局数监听
        warInstance.warCountProperty.addListener { observableValue: ObservableValue<out Number>?, number: Number?, t1: Number ->
            log.info(
                "已完成第 $t1 把游戏"
            )
            gameCount.text = warInstance.warCount.toString()
            winningPercentage.text = (String.format(
                "%.1f",
                warInstance.winCount.toDouble() / warInstance.warCount * 100.0
            ) + "%")
            gameTime.text = formatTime(warInstance.hangingTime)
            exp.text = warInstance.hangingEXP.toString()
        }
        deckStrategies.addListener(SetChangeListener { observable: SetChangeListener.Change<out DeckStrategy?>? ->
            reloadRunMode()
        } as SetChangeListener<in DeckStrategy?>)
        //        是否在下载中监听
        downloadingReadOnlyProperty().addListener { observable, oldValue, newValue ->
            Platform.runLater {
                updateBtn.isDisable = newValue
            }
        }
        //        监听日志自动滑到底部
        logVBox.heightProperty()
            .addListener { observable: ObservableValue<out Number>, oldValue: Number, newValue: Number ->
                if (isNotHoverLog) {
                    logScrollPane.vvalue = logScrollPane.vmax
                }
            }
        canUpdateReadOnlyProperty().addListener { observable, oldValue, newValue ->
            Platform.runLater {
                flushBtn.isVisible = !newValue
                flushBtn.isManaged = !newValue
                updateBtn.isVisible = newValue
                updateBtn.isManaged = newValue
            }
        }
        val btnPressedStyleClass = "btnPressed"
        pauseToggleGroup.selectedToggleProperty()
            .addListener { observableValue: ObservableValue<out Toggle?>?, toggle: Toggle?, t1: Toggle? ->
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
                        asyncSetPause(false)
                    } else if (t1 === pauseButton) {
                        pauseIco.color = "gray"
                        startIco.color = "black"
                        pauseButton.styleClass.add(btnPressedStyleClass)
                        val graphic = pauseButton.graphic as AbstractIco
                        graphic.color = "gray"
                        asyncSetPause(true)
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

        val vBox: VBox = object : VBox(label) {
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

    /**
     * 初始化挂机时间
     */
    fun initWorkDate() {
//        初始化挂机天
        val workDayMap = getWorkDay().stream().collect(Collectors.toMap(WorkDay::day, WorkDay::enabled))

        val workDayChildren = workDay.children
        for (workDayChild in workDayChildren) {
            if (workDayChild is CheckBox) {
                val enable = workDayMap[workDayChild.getUserData().toString()]
                workDayChild.isSelected = enable != null && enable
            }
        }
        //        初始化挂机段
        val workTimes: List<WorkTime> = getWorkTime()
        val length = min(workTimes.size.toDouble(), workDayChildren.size.toDouble()).toInt()
        val workTimeChildren = workTime.children
        for (i in 0 until length) {
            val timeHBox = workTimeChildren[i] as HBox
            val timeControls = (timeHBox.children[1] as HBox).children
            val time = workTimes[i]
            (timeControls[0] as Time).time = time.startTime
            (timeControls[2] as Time).time = time.endTime
            (timeHBox.children.first() as CheckBox).isSelected = time.enabled
        }
    }

    private fun changeSwitch(isPause: Boolean) {
        if (isPause) {
            pauseToggleGroup.selectToggle(pauseButton)
            accordion.expandedPane = titledPaneControl
        } else {
            pauseToggleGroup.selectToggle(startButton)
            accordion.expandedPane = titledPaneLog
        }
    }

    @FXML
    protected fun flushVersion() {
        val transition = RotateTransition(Duration.millis(1200.0), flushIco)
        transition.fromAngle = 0.0
        transition.toAngle = 360.0
        transition.cycleCount = Timeline.INDEFINITE
        try {
            transition.play()
            checkVersion()
            if (canUpdate) {
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
        var stage = getStage(WindowEnum.SETTINGS)
        if (stage == null) {
            stage = buildStage(WindowEnum.SETTINGS)
        }
        if (stage.getOwner() == null) {
            stage.initOwner(getStage(WindowEnum.MAIN))
        }
        stage.show()
    }

    @FXML
    protected fun updateVersion() {
        if (canUpdate) {
            val progress = SimpleDoubleProperty()
            val release = latestRelease ?: return

            downloadLatestRelease(
                false, progress
            ) { path: String? ->
                if (path == null) {
                    Platform.runLater {
                        createAlert(
                            String.format("新版本<%s>下载失败", release.tagName),
                            "",
                            rootPane.scene.window
                        ).show()
                    }
                } else {
                    Platform.runLater {
                        createAlert(
                            "新版本[" + release.tagName + "]下载完毕",
                            "现在更新？",
                            { event: ActionEvent? -> execUpdate(path) },
                            { event: ActionEvent? -> },
                            rootPane.scene.window
                        ).show()
                    }
                }
            }
        }
    }

    @FXML
    protected fun saveTime() {
//        检查挂机天
        val workDayChildren = workDay.children
        val workDays = ArrayList<WorkDay>()
        for (workDayChild in workDayChildren) {
            if (workDayChild is CheckBox) {
                workDays.add(WorkDay(workDayChild.getUserData().toString(), workDayChild.isSelected))
            }
        }
        storeWorkDay(workDays)
        //        检查挂机段
        val workTimeChildren = workTime.children
        val workTimes = ArrayList<WorkTime>()
        for (workTimeChild in workTimeChildren) {
            val hBox = workTimeChild as HBox
            val children = (hBox.children[1] as HBox).children
            val startTime = children[0] as Time
            val endTime = children[2] as Time
            val timeCheckBox = hBox.children[0] as CheckBox
            startTime.refresh()
            endTime.refresh()
            val time = WorkTime(startTime.time, endTime.time, timeCheckBox.isSelected)
            workTimes.add(time)
        }
        storeWorkTime(workTimes)

        initWorkDate()
        notificationManger.showSuccess("工作时间保存成功", 2)
    }

    @FXML
    protected fun resetStatistics(event: MouseEvent) {
        if (event.button != MouseButton.PRIMARY) return
        val modal = Modal(
            rootPane, null, "重置统计数据？",
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
            Runnable {})
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
        showStage(WindowEnum.VERSION_MSG, rootPane.scene.window)
    }

    companion object {
        //调试日志
        private val log: Logger = LoggerFactory.getLogger(MainController::class.java)


        private fun formatTime(time: Int): String {
            val timeStr = if (time == 0) {
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
}