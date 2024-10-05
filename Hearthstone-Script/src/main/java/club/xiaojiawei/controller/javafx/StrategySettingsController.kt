package club.xiaojiawei.controller.javafx

import club.xiaojiawei.utils.PropertiesUtil
import jakarta.annotation.Resource
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import org.springframework.stereotype.Component
import java.net.URL
import java.util.Properties

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class StrategySettingsController : Initializable {

    @Resource
    private val scriptConfiguration: Properties? = null

    @Resource
    private val propertiesUtil: PropertiesUtil? = null

    private var sceneListener: ChangeListener<Scene?>? = null

    @FXML
    private val rootPane: AnchorPane? = null

    @FXML
    private val actionIntervalField: NumberField? = null

    @FXML
    private val notificationManager: NotificationManager<*>? = null

    @FXML
    private val mouseMoveIntervalField: NumberField? = null

    @FXML
    private val mainVBox: VBox? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initStructure()
        initValue()
        listen()
    }

    private fun initStructure() {
        actionIntervalField.setMinValue("1")
        actionIntervalField.setPromptText("默认：" + ConfigEnum.MOUSE_ACTION_INTERVAL.defaultValue)
        mouseMoveIntervalField.setMinValue("1")
        mouseMoveIntervalField.setPromptText("默认：" + ConfigEnum.MOUSE_MOVE_INTERVAL.defaultValue)
    }

    private fun initValue() {
        actionIntervalField.setText(
            scriptConfiguration!!.getProperty(
                ConfigEnum.MOUSE_ACTION_INTERVAL.getKey(),
                ConfigEnum.MOUSE_ACTION_INTERVAL.defaultValue
            )
        )
        mouseMoveIntervalField.setText(
            scriptConfiguration.getProperty(
                ConfigEnum.MOUSE_MOVE_INTERVAL.getKey(),
                ConfigEnum.MOUSE_MOVE_INTERVAL.defaultValue
            )
        )
    }

    private fun listen() {
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene?>?, scene: Scene?, t1: Scene? ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)
    }

    @FXML
    protected fun apply(actionEvent: ActionEvent?) {
        if (saveProperties()) {
            notificationManager.showSuccess("应用成功", 2)
        } else {
            notificationManager.showWarn("应用失败", "值不合法", 2)
        }
    }

    @FXML
    protected fun save(actionEvent: ActionEvent?) {
        if (saveProperties()) {
            WindowUtil.hideStage(WindowEnum.SETTINGS)
        } else {
            notificationManager.showWarn("保存失败", "值不合法", 2)
        }
    }

    private fun saveProperties(): Boolean {
        val actionInterval: String? = actionIntervalField.getText()
        if (actionInterval == null || actionInterval.isBlank()) {
            return false
        }
        scriptConfiguration!!.setProperty(ConfigEnum.MOUSE_ACTION_INTERVAL.getKey(), actionInterval)
        val mouseMoveIntervalFieldText: String? = mouseMoveIntervalField.getText()
        if (mouseMoveIntervalFieldText == null || mouseMoveIntervalFieldText.isBlank()) {
            return false
        }
        scriptConfiguration.setProperty(ConfigEnum.MOUSE_MOVE_INTERVAL.getKey(), mouseMoveIntervalFieldText)
        propertiesUtil.storeScriptProperties()
        return true
    }
}
