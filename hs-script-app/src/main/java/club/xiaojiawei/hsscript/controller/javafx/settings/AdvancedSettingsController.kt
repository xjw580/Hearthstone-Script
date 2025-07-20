package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.ico.HelpIco
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.single.repository.GiteeRepository
import club.xiaojiawei.hsscript.controller.javafx.settings.view.AdvancedSettingsView
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.getPauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeExitHotKey
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeMouseControlMode
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storePauseHotKey
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import com.melloware.jintellitype.JIntellitypeConstants
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.Duration
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class AdvancedSettingsController : AdvancedSettingsView(), Initializable {

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
        mouseControlModeComboBox.setCellFactory {
            object : ListCell<MouseControlModeEnum?>() {
                private val ico = HelpIco()
                override fun updateItem(s: MouseControlModeEnum?, b: Boolean) {
                    super.updateItem(s, b)
                    if (s == null || b) return
                    contentDisplay = ContentDisplay.RIGHT
                    text = s.name
                    ico.color =
                        if (mouseControlModeComboBox.selectionModel.selectedItem != null && mouseControlModeComboBox.selectionModel.selectedItem === item) "white" else ""
                    graphic = Label().apply {
                        graphic = ico
                        tooltip = Tooltip(s.comment)
                    }
                }
            }
        }
        mouseControlModeComboBox.items.addAll(MouseControlModeEnum.entries.toTypedArray())
        mouseControlModeComboBox.value = ConfigExUtil.getMouseControlMode()
        val isDrive = mouseControlModeComboBox.value === MouseControlModeEnum.DRIVE
        refreshDriver.isVisible = isDrive
        refreshDriver.isManaged = isDrive

        val pauseKey = getPauseHotKey()
        if (pauseKey != null) {
            pauseHotKey.text = pauseKey.toString()
        }
        val exitKey = getExitHotKey()
        if (exitKey != null) {
            exitHotKey.text = exitKey.toString()
        }
    }

    private fun scrollTo(pane: Node) {
        pane.boundsInParent.let {
            val targetV = it.minY / (titledRootPane.height - scrollPane.viewportBounds.height)
            val sourceV = scrollPane.vvalue
            Timeline(
                KeyFrame(
                    Duration.millis(0.0), KeyValue(scrollPane.vvalueProperty(), sourceV)
                ),
                KeyFrame(
                    Duration.millis(200.0),
                    KeyValue(
                        scrollPane.vvalueProperty(),
                        targetV
                    )
                )
            ).run {
                forbidSetToggle = true
                onFinished = EventHandler {
                    forbidSetToggle = false
                }
                play()
            }
        }
    }

    private var forbidSetToggle = false
    private var versionMaxY = 0.0
    private var versionMinY = 0.0
    private var behaviorMaxY = 0.0
    private var behaviorMinY = 0.0
    private var systemMaxY = 0.0
    private var systemMinY = 0.0


    private fun updateY() {
        val diffH = titledRootPane.height - scrollPane.viewportBounds.height
        versionMaxY = versionPane.boundsInParent.maxY / diffH
        versionMinY = versionPane.boundsInParent.minY / diffH
        behaviorMaxY = behaviorPane.boundsInParent.maxY / diffH
        behaviorMinY = behaviorPane.boundsInParent.minY / diffH
        systemMaxY = systemPane.boundsInParent.maxY / diffH
        systemMinY = systemPane.boundsInParent.minY / diffH
    }

    private fun listen() {
        navigationBarToggle.selectedToggleProperty().addListener { _, oldToggle, newToggle ->
            newToggle ?: let {
                navigationBarToggle.selectToggle(oldToggle)
            }
        }
        scrollPane.vvalueProperty().addListener { _, oldValue, newValue ->
            if (forbidSetToggle) return@addListener
            updateY()
            val newV = newValue.toDouble()
            val oldV = oldValue.toDouble()
            if (newV - oldV > 0) {
                if (newV > systemMaxY) {
                    navigationBarToggle.selectToggle(versionNavigation)
                } else if (newV > behaviorMaxY) {
                    navigationBarToggle.selectToggle(systemNavigation)
                }
            } else {
                if (newV <= behaviorMinY) {
                    navigationBarToggle.selectToggle(behaviorNavigation)
                } else if (newV <= systemMinY) {
                    navigationBarToggle.selectToggle(systemNavigation)
                }
            }
        }
//        监听更新源
        updateSourceToggle.selectedToggleProperty().addListener { _, _, newValue ->
            putString(ConfigEnum.UPDATE_SOURCE, (newValue as ToggleButton).text)
        }
//        监听鼠标模式开关
        mouseControlModeComboBox.valueProperty()
            .addListener { observable, oldValue, newValue ->
                storeMouseControlMode(newValue)
                val isDrive = ConfigExUtil.getMouseControlMode() === MouseControlModeEnum.DRIVE
                refreshDriver.isVisible = isDrive
                refreshDriver.isManaged = isDrive
                topGameWindow.status =
                    (newValue === MouseControlModeEnum.EVENT || newValue === MouseControlModeEnum.DRIVE)
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

    private var modifier = 0

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

    @FXML
    protected fun scrollVersion(actionEvent: ActionEvent) {
        scrollTo(versionPane)
    }

    @FXML
    protected fun scrollBehavior(actionEvent: ActionEvent) {
        scrollTo(behaviorPane)
    }

    @FXML
    protected fun scrollSystem(actionEvent: ActionEvent) {
        scrollTo(systemPane)
    }

    @FXML
    protected fun refreshDriver(actionEvent: ActionEvent) {
        val res = CSystemDll.safeRefreshDriver()
        if (res >= 0) {
            notificationManager.showSuccess("刷新驱动成功", 2)
        } else {
            notificationManager.showError("刷新驱动失败", 2)
        }
    }

}