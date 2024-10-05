package club.xiaojiawei.controller.javafx

import club.xiaojiawei.utils.PropertiesUtil
import jakarta.annotation.Resource
import javafx.beans.value.ChangeListener
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.text.Text
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.util.Properties

/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
@Component
class InitSettingsController : Initializable {
    @FXML
    private val mainVBox: VBox? = null

    @FXML
    private val notificationManager: NotificationManager<*>? = null

    @FXML
    private val gamePath: Text? = null

    @FXML
    private val platformPath: Text? = null

    @FXML
    private val password: PasswordTextField? = null

    @Resource
    private val scriptConfiguration: Properties? = null

    @Resource
    private val propertiesUtil: PropertiesUtil? = null

    private var sceneListener: ChangeListener<Scene?>? = null

    @FXML
    private val rootPane: AnchorPane? = null

    @FXML
    private val apply: Button? = null

    @FXML
    private val save: Button? = null

    @FXML
    protected fun gameClicked() {
        val directoryChooser: DirectoryChooser = DirectoryChooser()
        directoryChooser.setTitle("选择" + ScriptStaticData.GAME_CN_NAME + "安装路径")
        val file: File? = directoryChooser.showDialog(Stage())
        if (file != null) {
            gamePath!!.setText(file.getAbsolutePath())
        }
    }

    @FXML
    protected fun platformClicked() {
        val fileChooser: FileChooser = FileChooser()
        fileChooser.setTitle("选择" + ScriptStaticData.PLATFORM_CN_NAME + "程序")
        fileChooser.getExtensionFilters().add(
            FileChooser.ExtensionFilter("程序", "*.exe")
        )
        val chooseFile: File? = fileChooser.showOpenDialog(Stage())
        if (chooseFile != null) {
            platformPath!!.setText(chooseFile.getAbsolutePath())
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
        scriptConfiguration!!.setProperty(ConfigEnum.PLATFORM_PASSWORD.getKey(), password.getText())
        propertiesUtil.storeScriptProperties()
        if (!propertiesUtil.storePlatformPath(platformPath!!.getText())) {
            notificationManager.showError(ScriptStaticData.PLATFORM_CN_NAME + "安装路径不正确,请重新选择", 3)
            initValue()
            return false
        }
        if (!propertiesUtil.storeGamePath(gamePath!!.getText())) {
            notificationManager.showError(ScriptStaticData.GAME_CN_NAME + "安装路径不正确,请重新选择", 3)
            initValue()
            return false
        }
        ScriptStaticData.setSetPath(true)
        return true
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene?>?, scene: Scene?, t1: Scene? ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)
    }

    private fun initValue() {
        gamePath!!.setText(scriptConfiguration!!.getProperty(ConfigEnum.GAME_PATH.getKey()))
        platformPath!!.setText(scriptConfiguration.getProperty(ConfigEnum.PLATFORM_PATH.getKey()))
        password.setText(scriptConfiguration.getProperty(ConfigEnum.PLATFORM_PASSWORD.getKey()))
    }
}
