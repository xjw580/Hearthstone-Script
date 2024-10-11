package club.xiaojiawei.hsscript.controller.javafx.view;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.ico.FlushIco;
import club.xiaojiawei.controls.ico.PauseIco;
import club.xiaojiawei.controls.ico.StartIco;
import club.xiaojiawei.enums.RunModeEnum;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author 肖嘉威
 * @date 2024/10/11 14:49
 */
abstract public class MainView implements Initializable {

    @FXML
    protected StartIco startIco;
    @FXML
    protected PauseIco pauseIco;
    @FXML
    protected ToggleGroup pauseToggleGroup;
    @FXML
    protected StackPane rootPane;
    @FXML
    protected NotificationManager<Object> notificationManger;
    @FXML
    protected ScrollPane logScrollPane;
    @FXML
    protected Button updateBtn;
    @FXML
    protected Button flushBtn;
    @FXML
    protected FlushIco flushIco;
    @FXML
    protected Text versionText;
    @FXML
    protected VBox logVBox;
    @FXML
    protected Accordion accordion;
    @FXML
    protected ToggleButton startButton;
    @FXML
    protected ToggleButton pauseButton;
    @FXML
    protected TitledPane titledPaneLog;
    @FXML
    protected Text gameCount;
    @FXML
    protected Text winningPercentage;
    @FXML
    protected Text gameTime;
    @FXML
    protected Text exp;
    @FXML
    protected ComboBox<RunModeEnum> runModeBox;
    @FXML
    protected ComboBox<DeckStrategy> deckBox;
    @FXML
    protected TilePane workDay;
    @FXML
    protected VBox workTime;
    @FXML
    protected ProgressBar downloadProgress;
    @FXML
    protected TitledPane titledPaneControl;

}
