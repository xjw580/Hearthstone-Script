package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.PasswordTextField
import club.xiaojiawei.hsscript.data.GAME_CN_NAME
import club.xiaojiawei.hsscript.data.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.data.haveProgramPath
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storeGamePath
import club.xiaojiawei.hsscript.utils.ConfigExUtil.storePlatformPath
import club.xiaojiawei.hsscript.utils.ConfigUtil.getInt
import club.xiaojiawei.hsscript.utils.ConfigUtil.getString
import club.xiaojiawei.hsscript.utils.ConfigUtil.putInt
import club.xiaojiawei.hsscript.utils.ConfigUtil.putString
import club.xiaojiawei.hsscript.utils.WindowUtil.hideStage
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
class InitSettingsController : Initializable {
    @FXML
    lateinit var deckPosComboBox: ComboBox<String>

    @FXML
    lateinit var mainVBox: VBox

    @FXML
    lateinit var notificationManager: NotificationManager<Any>

    @FXML
    lateinit var gamePath: Text

    @FXML
    lateinit var platformPath: Text

    @FXML
    lateinit var password: PasswordTextField

    @FXML
    lateinit var rootPane: StackPane

    @FXML
    lateinit var apply: Button

    @FXML
    lateinit var save: Button

    private var sceneListener: ChangeListener<Scene>? = null

    @FXML
    protected fun gameClicked() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "选择" + GAME_CN_NAME + "安装路径"
        val file = directoryChooser.showDialog(Stage())
        if (file != null) {
            gamePath.text = file.absolutePath
        }
    }

    @FXML
    protected fun platformClicked() {
        val fileChooser = FileChooser()
        fileChooser.title = "选择" + PLATFORM_CN_NAME + "程序"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("程序", "*.exe")
        )
        val chooseFile = fileChooser.showOpenDialog(Stage())
        if (chooseFile != null) {
            platformPath.text = chooseFile.absolutePath
        }
    }

    @FXML
    protected fun apply() {
        if (checkConfiguration()) {
            notificationManager.showSuccess("应用成功", 2)
        }
    }

    @FXML
    protected fun save() {
        if (checkConfiguration()) {
            hideStage(WindowEnum.SETTINGS)
        }
    }

    private fun checkConfiguration(): Boolean {
        putString(ConfigEnum.PLATFORM_PASSWORD, password.text, true)
        if (!storePlatformPath(platformPath.text)) {
            notificationManager.showError(PLATFORM_CN_NAME + "安装路径不正确,请重新选择", 3)
            initValue()
            return false
        }
        if (!storeGamePath(gamePath.text)) {
            notificationManager.showError(GAME_CN_NAME + "安装路径不正确,请重新选择", 3)
            initValue()
            return false
        }
        haveProgramPath = true
        return true
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        addListener()
    }

    private fun addListener() {
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene>?, scene: Scene?, t1: Scene ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)
        deckPosComboBox.valueProperty()
            .addListener { observable: ObservableValue<out String>?, oldValue: String?, newValue: String? ->
                if (newValue.isNullOrBlank()) return@addListener
                putInt(ConfigEnum.CHOOSE_DECK_POS, newValue.toInt(), true)
                notificationManager.showSuccess("修改成功", 2)
            }
    }

    private fun initValue() {
        gamePath.text = getString(ConfigEnum.GAME_PATH)
        platformPath.text = getString(ConfigEnum.PLATFORM_PATH)
        password.text = getString(ConfigEnum.PLATFORM_PASSWORD)
        deckPosComboBox.value = getInt(ConfigEnum.CHOOSE_DECK_POS).toString()
    }
}