package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.controls.ico.HelpIco
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.single.repository.GiteeRepository
import club.xiaojiawei.hsscript.data.MOUSE_DRIVE_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getPauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeMouseControlMode
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storePauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeTopGameWindow
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil.getBoolean
import club.xiaojiawei.hsscript.utils.ConfigUtil.putBoolean
import club.xiaojiawei.hsscript.utils.WindowUtil
import com.melloware.jintellitype.JIntellitypeConstants
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import java.io.File
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class AdvancedSettingsController : Initializable {

    @FXML
    protected lateinit var mouseControlModeComboBox: ComboBox<MouseControlModeEnum>

    @FXML
    protected lateinit var githubUpdateSource: RadioButton

    @FXML
    protected lateinit var giteeUpdateSource: RadioButton

    @FXML
    protected lateinit var updateSourceToggle: ToggleGroup

    @FXML
    protected lateinit var pauseHotKey: TextField

    @FXML
    protected lateinit var exitHotKey: TextField

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var updateDev: Switch

    @FXML
    protected lateinit var autoUpdate: Switch

    @FXML
    protected lateinit var runningMinimize: Switch

    @FXML
    protected lateinit var topGameWindow: Switch

    @FXML
    protected lateinit var sendNotice: Switch

    @FXML
    protected lateinit var useProxy: Switch

    @FXML
    protected lateinit var rootPane: StackPane

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        listen()
    }

    private fun initValue() {
        val repositoryList = ConfigExUtil.getUpdateSourceList()
        if (repositoryList.isEmpty() || repositoryList.first() == GiteeRepository) {
            giteeUpdateSource.isSelected = true
        } else {
            githubUpdateSource.isSelected = true
        }
        updateDev.status = getBoolean(ConfigEnum.UPDATE_DEV)
        autoUpdate.status = getBoolean(ConfigEnum.AUTO_UPDATE)
        runningMinimize.status = getBoolean(ConfigEnum.RUNNING_MINIMIZE)
        mouseControlModeComboBox.setCellFactory {
            object : ListCell<MouseControlModeEnum?>() {
                override fun updateItem(s: MouseControlModeEnum?, b: Boolean) {
                    super.updateItem(s, b)
                    if (s == null || b) return
                    contentDisplay = ContentDisplay.RIGHT
                    text = s.name
                    graphic = Label().apply {
                        graphic = HelpIco()
                        tooltip = Tooltip(s.comment)
                    }
                }
            }
        }
        mouseControlModeComboBox.items.addAll(MouseControlModeEnum.values())
        mouseControlModeComboBox.value = ConfigExUtil.getMouseControlMode()
        topGameWindow.status = getBoolean(ConfigEnum.TOP_GAME_WINDOW)
        sendNotice.status = getBoolean(ConfigEnum.SEND_NOTICE)
        useProxy.status = getBoolean(ConfigEnum.USE_PROXY)

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
//        监听更新源
        updateSourceToggle.selectedToggleProperty().addListener { _, _, newValue ->
            ConfigUtil.putString(ConfigEnum.UPDATE_SOURCE, (newValue as ToggleButton).text)
        }
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
        var mouseControlModeComboBoxCallback = false
        //        监听控制开关
        mouseControlModeComboBox.valueProperty()
            .addListener { observable, oldValue, newValue ->
                if (mouseControlModeComboBoxCallback) {
                    mouseControlModeComboBoxCallback = false
                    return@addListener
                }
                val exec = {
                    storeMouseControlMode(
                        newValue
                    )
                    topGameWindow.status =
                        (newValue === MouseControlModeEnum.EVENT || newValue === MouseControlModeEnum.DRIVE)
                }
                if (newValue === MouseControlModeEnum.DRIVE) {
                    if (File(MOUSE_DRIVE_PATH).exists()) {
                        exec()
                    } else {
                        WindowUtil.createAlert("${MouseControlModeEnum.DRIVE}模式需要安装驱动（安装时请提前关闭杀毒软件或windows defender）", "是否安装", {
                            exec()
                        }, {
                            mouseControlModeComboBoxCallback = true
                            mouseControlModeComboBox.value = oldValue
                        }, rootPane.scene.window).show()
                    }
                } else {
                    exec()
                }
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
        //        监听使用系统代理开关
        useProxy.statusProperty()
            .addListener { observable, oldValue, newValue ->
                putBoolean(
                    ConfigEnum.USE_PROXY,
                    newValue, true
                )
            }

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