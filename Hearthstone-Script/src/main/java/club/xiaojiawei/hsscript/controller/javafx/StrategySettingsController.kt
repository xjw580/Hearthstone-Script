package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.NumberField
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.bean.CommonCardAction
import club.xiaojiawei.hsscript.data.reloadScriptData
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil.getBoolean
import club.xiaojiawei.hsscript.utils.ConfigUtil.getString
import club.xiaojiawei.hsscript.utils.ConfigUtil.putBoolean
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import club.xiaojiawei.hsscript.utils.ConfigUtil.store
import club.xiaojiawei.hsscript.utils.MouseUtil
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class StrategySettingsController : Initializable {
    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var actionIntervalField: NumberField

    @FXML
    protected lateinit var moveSpeedField: NumberField

    @FXML
    protected lateinit var matchMaximumTimeField: NumberField

    @FXML
    protected lateinit var idleMaximumTimeField: NumberField

    @FXML
    protected lateinit var logLimitField: NumberField

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var mainVBox: VBox

    @FXML
    protected lateinit var randomEventSwitch: Switch

    @FXML
    protected lateinit var randomEmotionSwitch: Switch

    @FXML
    protected lateinit var onlyRobotSwitch: Switch

    @FXML
    protected lateinit var autoSurrenderField: NumberField

    private var sceneListener: ChangeListener<Scene>? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initActionInterval()
        initMoveSpeed()
        initMatchMaximumTime()
        initIdleMaximumTime()
        initLogLimit()
        initRandomEvent()
        initRandomEmotion()
        initOnlyRobot()
        initAutoSurrender()

        listen()
    }

    private fun initActionInterval() {
        val key = ConfigEnum.MOUSE_ACTION_INTERVAL
        actionIntervalField.setMinValue("1")
        actionIntervalField.promptText = "默认：" + key.defaultValue
        actionIntervalField.tooltip = Tooltip("默认：" + key.defaultValue)
        actionIntervalField.text = getString(key)
    }

    private fun initMoveSpeed() {
        val key = ConfigEnum.PAUSE_STEP
        moveSpeedField.setMinValue("1")
        moveSpeedField.promptText = "默认：" + key.defaultValue
        moveSpeedField.tooltip = Tooltip("默认：" + key.defaultValue)
        moveSpeedField.text = getString(key)
    }

    private fun initMatchMaximumTime() {
        val key = ConfigEnum.MATCH_MAXIMUM_TIME
        matchMaximumTimeField.setMinValue("1")
        matchMaximumTimeField.promptText = "默认：" + key.defaultValue
        matchMaximumTimeField.tooltip = Tooltip("默认：" + key.defaultValue)
        matchMaximumTimeField.text = getString(key)
    }

    private fun initIdleMaximumTime() {
        val key = ConfigEnum.IDLE_MAXIMUM_TIME
        idleMaximumTimeField.setMinValue("1")
        idleMaximumTimeField.promptText = "默认：" + key.defaultValue
        idleMaximumTimeField.tooltip = Tooltip("默认：" + key.defaultValue)
        idleMaximumTimeField.text = getString(key)
    }

    private fun initLogLimit() {
        val key = ConfigEnum.GAME_LOG_LIMIT
        logLimitField.setMinValue("1")
        logLimitField.setMaxValue("102400")
        logLimitField.promptText = "默认：" + key.defaultValue
        logLimitField.tooltip = Tooltip("默认：" + key.defaultValue)
        logLimitField.text = getString(key)
    }

    private fun initRandomEvent() {
        val key = ConfigEnum.RANDOM_EVENT
        randomEventSwitch.status = getBoolean(key)
        randomEventSwitch.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    key,
                    newValue, true
                )
                notificationManager.showSuccess("修改成功", 1)
            }
    }

    private fun initRandomEmotion() {
        val key = ConfigEnum.RANDOM_EMOTION
        randomEmotionSwitch.status = getBoolean(key)
        randomEmotionSwitch.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    key,
                    newValue, true
                )
                notificationManager.showSuccess("修改成功", 1)
            }
    }

    private fun initOnlyRobot() {
        val key = ConfigEnum.ONLY_ROBOT
        onlyRobotSwitch.status = getBoolean(key)
        onlyRobotSwitch.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    key,
                    newValue, true
                )
                notificationManager.showSuccess("修改成功", 1)
            }
    }

    private fun initAutoSurrender() {
        val key = ConfigEnum.AUTO_SURRENDER
        autoSurrenderField.setMinValue("-1")
        val defaultText = "默认：" + key.defaultValue
        autoSurrenderField.promptText = defaultText
        autoSurrenderField.tooltip = Tooltip(defaultText)
        autoSurrenderField.text = getString(key)
    }


    private fun listen() {
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene>?, scene: Scene?, t1: Scene ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)
    }

    @FXML
    protected fun apply(actionEvent: ActionEvent?) {
        if (saveProperties()) {
            notificationManager.showSuccess("应用成功，即刻生效", 2)
        }
    }

    @FXML
    protected fun save(actionEvent: ActionEvent?) {
        if (saveProperties()) {
            hideStage(WindowEnum.SETTINGS)
        }
    }

    private fun saveProperties(): Boolean {
        var actionInterval = actionIntervalField.text
        if (actionInterval == null || actionInterval.isBlank()) {
            actionInterval = ConfigEnum.MOUSE_ACTION_INTERVAL.defaultValue
            actionIntervalField.text = actionInterval
            return false
        }
        putString(ConfigEnum.MOUSE_ACTION_INTERVAL, actionInterval, false)

        var moveSpeed = moveSpeedField.text
        if (moveSpeed == null || moveSpeed.isBlank()) {
            moveSpeed = ConfigEnum.PAUSE_STEP.defaultValue
            moveSpeedField.text = moveSpeed
        }
        putString(ConfigEnum.PAUSE_STEP, moveSpeed, false)

        var matchMaximumTime = matchMaximumTimeField.text
        if (matchMaximumTime == null || matchMaximumTime.isBlank()) {
            matchMaximumTime = ConfigEnum.MATCH_MAXIMUM_TIME.defaultValue
            matchMaximumTimeField.text = matchMaximumTime
        }
        putString(ConfigEnum.MATCH_MAXIMUM_TIME, matchMaximumTime, false)

        var idleMaximumTime = idleMaximumTimeField.text
        if (idleMaximumTime == null || idleMaximumTime.isBlank()) {
            idleMaximumTime = ConfigEnum.IDLE_MAXIMUM_TIME.defaultValue
            logLimitField.text = idleMaximumTime
        }
        putString(ConfigEnum.IDLE_MAXIMUM_TIME, idleMaximumTime, false)

        var logLimit = logLimitField.text
        if (logLimit == null || logLimit.isBlank()) {
            logLimit = ConfigEnum.GAME_LOG_LIMIT.defaultValue
            logLimitField.text = logLimit
        }
        putString(ConfigEnum.GAME_LOG_LIMIT, logLimit, false)

        var autoSurrender = autoSurrenderField.text
        if (autoSurrender == null || autoSurrender.isBlank() || autoSurrender == "-") {
            autoSurrender = ConfigEnum.AUTO_SURRENDER.defaultValue
            autoSurrenderField.text = autoSurrender
        }
        putString(ConfigEnum.AUTO_SURRENDER, autoSurrender, false)

        store()
        CommonCardAction.reload()
        reloadScriptData()
        MouseUtil.reload()
        return true
    }
}