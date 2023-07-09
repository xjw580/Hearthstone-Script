package club.xiaojiawei.controller;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.RUN_MODE_KEY;

/**
 * @author zerg
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class DashboardController implements Initializable {

    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private VBox logVBox;
    @FXML
    private Accordion accordion;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private TitledPane titledPaneLog;
    @FXML
    private Text gameCount;
    @FXML
    private Switch logSwitch;
    @FXML
    private ComboBox runModeBox;
    @FXML
    private ComboBox deckBox;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptProperties;
    @Resource
    private InitSettingsController initSettingsController;
    @Resource
    private SystemUtil systemUtil;
    @Autowired
    private PropertiesUtil propertiesUtil;
    public void expandedLogPane(){
        accordion.setExpandedPane(titledPaneLog);
    }
    @FXML
    protected void start(){
        isPause.get().set(false);
    }
    @FXML
    protected void pause(){
        isPause.get().set(true);
    }

    @FXML
    protected void settings() {
        initSettingsController.showStage();
    }
    public static VBox logVBoxBack;
    public static Accordion accordionBack;
    public static Switch logSwitchBack;
    private RunModeEnum currentRunMode;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logVBoxBack = logVBox;
        accordionBack = accordion;
        logSwitchBack = logSwitch;
        DeckEnum deckEnum = DeckEnum.valueOf(scriptProperties.getProperty(DECK_KEY.getKey()));
        currentRunMode = deckEnum.getRunMode();
        ObservableList runModeBoxItems = runModeBox.getItems();
        RunModeEnum[] values = RunModeEnum.values();
        DeckEnum[] deckEnums = DeckEnum.values();
        ObservableList deckBoxItems = deckBox.getItems();
        for (RunModeEnum value : values) {
            if (value.isEnable()){
                runModeBoxItems.add(value.getComment());
            }
            if (currentRunMode == value){
                runModeBox.getSelectionModel().select(currentRunMode.getComment());
                for (DeckEnum anEnum : deckEnums) {
                    if (anEnum.getRunMode() == currentRunMode){
                        deckBoxItems.add(anEnum.getComment());
                    }
                }
            }
        }
        deckBox.getSelectionModel().select(deckEnum.getComment());
        runModeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for (RunModeEnum value : RunModeEnum.values()) {
                if (Objects.equals(value.getComment(), newValue)){
                    currentRunMode = value;
                }
            }
            deckBoxItems.clear();
            for (DeckEnum anEnum : deckEnums) {
                if (anEnum.getRunMode() == currentRunMode){
                    deckBoxItems.add(anEnum.getComment());
                }
            }
        });
        deckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                scriptProperties.setProperty(RUN_MODE_KEY.getKey(), currentRunMode.getValue());
                for (DeckEnum anEnum : deckEnums) {
                    if (Objects.equals(newValue, anEnum.getComment())){
                        scriptProperties.setProperty(DECK_KEY.getKey(), anEnum.getValue());
                    }
                }
                propertiesUtil.storeScriptProperties();
                systemUtil.notice("挂机卡组更换成功");
                log.info("挂机卡组更换成功");
            }
        });
//        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> logScrollPane.setVvalue(logScrollPane.getVmax()));
//        监听局数
        War.warCount.addListener((observable, oldValue, newValue) -> {
            log.info("已完成第 " + newValue + " 把游戏");
            gameCount.setText(newValue.toString());
        });
    }

    public void changeSwitch(boolean value){
        pauseButton.setDisable(value);
        startButton.setDisable(!value);
    }

}