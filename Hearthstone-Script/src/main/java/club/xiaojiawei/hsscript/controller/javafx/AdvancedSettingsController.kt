package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getPauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeControlMode
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storePauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeTopGameWindow
import club.xiaojiawei.hsscript.utils.ConfigUtil.getBoolean
import club.xiaojiawei.hsscript.utils.ConfigUtil.putBoolean
import com.melloware.jintellitype.JIntellitypeConstants
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class AdvancedSettingsController : Initializable {
    @FXML
    lateinit var pauseHotKey: TextField

    @FXML
    lateinit var exitHotKey: TextField

    @FXML
    lateinit var mainVBox: VBox

    @FXML
    lateinit var notificationManager: NotificationManager<Any>

    @FXML
    lateinit var updateDev: Switch

    @FXML
    lateinit var autoUpdate: Switch

    @FXML
    lateinit var runningMinimize: Switch

    @FXML
    lateinit var controlMode: Switch

    @FXML
    lateinit var topGameWindow: Switch

    @FXML
    lateinit var sendNotice: Switch

    @FXML
    lateinit var rootPane: StackPane

    private var sceneListener: ChangeListener<Scene>? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        listen()
    }

    private fun initValue() {
        updateDev.status = getBoolean(ConfigEnum.UPDATE_DEV)
        autoUpdate.status = getBoolean(ConfigEnum.AUTO_UPDATE)
        runningMinimize.status = getBoolean(ConfigEnum.RUNNING_MINIMIZE)
        controlMode.status = getBoolean(ConfigEnum.CONTROL_MODE)
        topGameWindow.status = getBoolean(ConfigEnum.TOP_GAME_WINDOW)
        sendNotice.status = getBoolean(ConfigEnum.SEND_NOTICE)

        val pauseKey = getPauseHotKey()
        if (pauseKey != null) {
            pauseHotKey.text = pauseKey.toString()
        }
        val exitKey = getExitHotKey()
        if (exitKey != null) {
            exitHotKey.text = exitKey.toString()
        }
    }

    private fun listen() {
//        监听更新开发版开关
        updateDev.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.UPDATE_DEV,
                    newValue, true
                )
            }
        //        监听自动更新开关
        autoUpdate.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.AUTO_UPDATE,
                    newValue, true
                )
            }
        //        监听运行最小化开关
        runningMinimize.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.RUNNING_MINIMIZE,
                    newValue, true
                )
            }
        //        监听控制开关
        controlMode.statusProperty()
            .addListener { observable, oldValue, newValue ->
                storeControlMode(
                    newValue
                )
                topGameWindow.status = newValue
            }
        //        监听置顶游戏窗口开关
        topGameWindow.statusProperty()
            .addListener { observable, oldValue, newValue ->
                storeTopGameWindow(
                    newValue
                )
            }
        //        监听发送通知开关
        sendNotice.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.SEND_NOTICE,
                    newValue, true
                )
            }
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene>?, scene: Scene?, t1: Scene ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)

        pauseHotKey.onKeyPressed =
            EventHandler { event: KeyEvent ->
                val hotKey = plusModifier(event)
                if (hotKey != null) {
                    if (hotKey.keyCode == 0) {
                        pauseHotKey.text = ""
                        storePauseHotKey(hotKey)
                        GlobalHotkeyListener.reload()
                        notificationManager.showSuccess("开始/暂停热键热键已删除", 2)
                    } else {
                        pauseHotKey.text = hotKey.toString()
                        storePauseHotKey(hotKey)
                        GlobalHotkeyListener.reload()
                        notificationManager.showSuccess("开始/暂停热键已修改", 2)
                    }
                }
            }
        pauseHotKey.onKeyReleased =
            EventHandler { event: KeyEvent ->
                this.reduceModifier(
                    event
                )
            }
        pauseHotKey.focusedProperty()
            .addListener { observable, oldValue, newValue ->
                if (!newValue) {
                    modifier = 0
                }
            }

        exitHotKey.onKeyPressed =
            EventHandler { event: KeyEvent ->
                val hotKey = plusModifier(event)
                if (hotKey != null) {
                    if (hotKey.keyCode == 0) {
                        exitHotKey.text = ""
                        storeExitHotKey(hotKey)
                        GlobalHotkeyListener.reload()
                        notificationManager.showSuccess("退出热键已删除", 2)
                    } else {
                        exitHotKey.text = hotKey.toString()
                        storeExitHotKey(hotKey)
                        GlobalHotkeyListener.reload()
                        notificationManager.showSuccess("退出热键已修改", 2)
                    }
                }
            }
        exitHotKey.onKeyReleased =
            EventHandler { event: KeyEvent ->
                this.reduceModifier(
                    event
                )
            }
        exitHotKey.focusedProperty()
            .addListener { observable, oldValue, newValue ->
                if (!newValue) {
                    modifier = 0
                }
            }
    }

    private fun plusModifier(event: KeyEvent): HotKey? {
        if (event.code == KeyCode.ALT) {
            modifier += JIntellitypeConstants.MOD_ALT
        } else if (event.code == KeyCode.CONTROL) {
            modifier += JIntellitypeConstants.MOD_CONTROL
        } else if (event.code == KeyCode.SHIFT) {
            modifier += JIntellitypeConstants.MOD_SHIFT
        } else if (event.code == KeyCode.WINDOWS) {
            modifier += JIntellitypeConstants.MOD_WIN
        } else if (event.code == KeyCode.BACK_SPACE) {
            return HotKey()
        } else {
            val code = event.code.code
            if (code >= KeyCode.A.code && code <= KeyCode.Z.code) {
                return HotKey(modifier, code)
            }
        }
        return null
    }

    private fun reduceModifier(event: KeyEvent) {
        if (event.code == KeyCode.ALT) {
            modifier -= JIntellitypeConstants.MOD_ALT
        } else if (event.code == KeyCode.CONTROL) {
            modifier -= JIntellitypeConstants.MOD_CONTROL
        } else if (event.code == KeyCode.SHIFT) {
            modifier -= JIntellitypeConstants.MOD_SHIFT
        } else if (event.code == KeyCode.WINDOWS) {
            modifier -= JIntellitypeConstants.MOD_WIN
        }
    }

    private var modifier = 0
}