package club.xiaojiawei.controller.javafx

import club.xiaojiawei.controller.web.DashboardController
import jakarta.annotation.Resource
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.Initializable
import org.springframework.stereotype.Component
import java.net.URL
import java.util.Properties

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
class AdvancedSettingsController : Initializable {
    @FXML
    private val mainVBox: VBox? = null

    @FXML
    private val notificationManager: NotificationManager<Any?>? = null

    @FXML
    private val webSwitch: Switch? = null

    @FXML
    private val strategySwitch: Switch? = null

    @FXML
    private val verifySwitch: Switch? = null

    @FXML
    private val psw: PasswordTextField? = null

    @FXML
    private val updateDev: Switch? = null

    @FXML
    private val autoUpdate: Switch? = null

    @FXML
    private val staticCursor: Switch? = null

    @FXML
    private val sendNotice: Switch? = null

    @Resource
    private val scriptConfiguration: Properties? = null

    @Resource
    private val propertiesUtil: PropertiesUtil? = null

    private var sceneListener: ChangeListener<Scene?>? = null

    @FXML
    private val rootPane: AnchorPane? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        listen()
    }

    private fun initValue() {
        webSwitch.setStatus(scriptConfiguration!!.getProperty(AUTO_OPEN_WEB.getKey()) == "true")
        strategySwitch.setStatus(scriptConfiguration.getProperty(ConfigEnum.STRATEGY.getKey()) == "true")
        verifySwitch.setStatus(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()) == "true")
        psw.setText(scriptConfiguration.getProperty(VERIFY_PASSWORD.getKey()))
        updateDev.setStatus(scriptConfiguration.getProperty(ConfigEnum.UPDATE_DEV.getKey()) == "true")
        autoUpdate.setStatus(scriptConfiguration.getProperty(ConfigEnum.AUTO_UPDATE.getKey()) == "true")
        staticCursor.setStatus(scriptConfiguration.getProperty(STATIC_CURSOR.getKey()) == "true")
        sendNotice.setStatus(scriptConfiguration.getProperty(ConfigEnum.SEND_NOTICE.getKey()) == "true")
    }

    private fun listen() {
//        监听web界面开关
        webSwitch.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(AUTO_OPEN_WEB.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听策略开关
        strategySwitch.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(ConfigEnum.STRATEGY.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听安全验证开关
        verifySwitch.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(ENABLE_VERIFY.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听更新开发版开关
        updateDev.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(ConfigEnum.UPDATE_DEV.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听自动更新开关
        autoUpdate.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(ConfigEnum.AUTO_UPDATE.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听静态光标开关
        staticCursor.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(STATIC_CURSOR.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        //        监听发送通知开关
        sendNotice.statusProperty()
            .addListener(ChangeListener { observable: ObservableValue<out Boolean?>?, oldValue: Boolean?, newValue: Boolean? ->
                scriptConfiguration!!.setProperty(ConfigEnum.SEND_NOTICE.getKey(), newValue.toString())
                propertiesUtil.storeScriptProperties()
            })
        sceneListener = ChangeListener { observableValue: ObservableValue<out Scene?>?, scene: Scene?, t1: Scene? ->
            mainVBox.prefWidthProperty().bind(t1.widthProperty())
            mainVBox.sceneProperty().removeListener(sceneListener)
        }
        mainVBox.sceneProperty().addListener(sceneListener)
    }

    @FXML
    protected fun saveVerifyPassword(event: Event?) {
        scriptConfiguration!!.setProperty(VERIFY_PASSWORD.getKey(), psw.getText())
        propertiesUtil.storeScriptProperties()
        DashboardController.TOKEN_SET.clear()
        notificationManager.showSuccess("Web密码保存成功", 2)
    }

    @FXML
    protected fun openMeasureUtil(actionEvent: ActionEvent?) {
        MeasureApplication.startStage(Stage())
    }
}
