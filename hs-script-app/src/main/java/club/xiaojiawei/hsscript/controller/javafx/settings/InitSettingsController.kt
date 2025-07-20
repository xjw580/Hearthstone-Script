package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.PasswordTextField
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
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
    protected lateinit var chooseDeckPosPane: HBox

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var gamePath: Text

    @FXML
    protected lateinit var platformPath: Text

    @FXML
    protected lateinit var password: PasswordTextField

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var apply: Button

    @FXML
    protected lateinit var save: Button

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
            WindowUtil.hideStage(WindowEnum.SETTINGS)
        }
    }

    private fun checkConfiguration(): Boolean {
        var res = true
        ConfigUtil.putString(ConfigEnum.PLATFORM_PASSWORD, password.text, true)
        if (ConfigExUtil.storePlatformPath(platformPath.text)) {
            ScriptStatus.isValidPlatformProgramPath = true
        } else {
            notificationManager.showError(PLATFORM_CN_NAME + "程序路径不正确,请重新选择", 3)
            initValue()
            res = false
        }
        if (ConfigExUtil.storeGamePath(gamePath.text)) {
            ScriptStatus.isValidGameInstallPath = true
        } else {
            notificationManager.showError(GAME_CN_NAME + "安装路径不正确,请重新选择", 3)
            initValue()
            res = false
        }
        return res
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        addListener()
    }

    private fun addListener() {
    }


    private fun initValue() {
        gamePath.text = ConfigUtil.getString(ConfigEnum.GAME_PATH)
        platformPath.text = ConfigUtil.getString(ConfigEnum.PLATFORM_PATH)
        password.text = ConfigUtil.getString(ConfigEnum.PLATFORM_PASSWORD)
        val deckPosCheckBoxList = mutableListOf<CheckBox>()
        val posList = ConfigExUtil.getChooseDeckPos()
        for (i in 1 until 10) {
            val checkBox = CheckBox(i.toString()).apply {
                styleClass.addAll("check-box-ui", "check-box-ui-main")
                isSelected = posList.contains(i) == true
                selectedProperty().addListener { observable, oldValue, newValue ->
                    ConfigExUtil.storeChooseDeckPos(
                        deckPosCheckBoxList.withIndex()
                            .filter { it.value.isSelected }
                            .map { it.index + 1 }
                    )
                }
            }
            chooseDeckPosPane.children.add(checkBox)
            deckPosCheckBoxList.add(checkBox)
        }
    }
}