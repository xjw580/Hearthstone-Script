package club.xiaojiawei.hsscript.controller.javafx.settings.view

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.hsscript.component.ConfigSwitch
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.RadioButton
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

/**
 * @author 肖嘉威
 * @date 2025/3/7 16:11
 */
open class AdvancedSettingsView {

    @FXML
    protected lateinit var systemTitled: TitledPane

    @FXML
    protected lateinit var behaviorTitled: TitledPane

    @FXML
    protected lateinit var versionTitled: TitledPane

    @FXML
    protected lateinit var titledRootPane: VBox

    @FXML
    protected lateinit var scrollPane: ScrollPane

    @FXML
    protected lateinit var versionPane: Group

    @FXML
    protected lateinit var behaviorPane: Group

    @FXML
    protected lateinit var systemPane: Group

    @FXML
    protected lateinit var systemNavigation: ToggleButton

    @FXML
    protected lateinit var behaviorNavigation: ToggleButton

    @FXML
    protected lateinit var versionNavigation: ToggleButton

    @FXML
    protected lateinit var navigationBarToggle: ToggleGroup

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
    protected lateinit var topGameWindow: ConfigSwitch

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var refreshDriver: Button

}