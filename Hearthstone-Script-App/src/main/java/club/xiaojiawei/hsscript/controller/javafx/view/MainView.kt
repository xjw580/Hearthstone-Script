package club.xiaojiawei.hsscript.controller.javafx.view

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ico.FlushIco
import club.xiaojiawei.controls.ico.PauseIco
import club.xiaojiawei.controls.ico.StartIco
import club.xiaojiawei.enums.RunModeEnum
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text

/**
 * @author 肖嘉威
 * @date 2024/10/11 14:49
 */
abstract class MainView : Initializable {

    @FXML
    protected lateinit var startIco: StartIco

    @FXML
    protected lateinit var pauseIco: PauseIco

    @FXML
    protected lateinit var pauseToggleGroup: ToggleGroup

    @FXML
    protected lateinit var rootPane: StackPane

    @FXML
    protected lateinit var notificationManger: NotificationManager<Any>

    @FXML
    protected lateinit var logScrollPane: ScrollPane

    @FXML
    protected lateinit var updateBtn: Button

    @FXML
    protected lateinit var flushBtn: Button

    @FXML
    protected lateinit var flushIco: FlushIco

    @FXML
    protected lateinit var versionText: Text

    @FXML
    protected lateinit var logVBox: VBox

    @FXML
    protected lateinit var accordion: Accordion

    @FXML
    protected lateinit var startButton: ToggleButton

    @FXML
    protected lateinit var pauseButton: ToggleButton

    @FXML
    protected lateinit var titledPaneLog: TitledPane

    @FXML
    protected lateinit var gameCount: Text

    @FXML
    protected lateinit var winningPercentage: Text

    @FXML
    protected lateinit var gameTime: Text

    @FXML
    protected lateinit var exp: Text

    @FXML
    protected lateinit var runModeBox: ComboBox<RunModeEnum>

    @FXML
    protected lateinit var deckStrategyBox: ComboBox<DeckStrategy>

    @FXML
    protected lateinit var downloadProgress: ProgressBar

    @FXML
    protected lateinit var titledPaneControl: TitledPane

    @FXML
    protected lateinit var workTimePane: VBox

    @FXML
    protected lateinit var workTimeRuleSetId: Label

}
