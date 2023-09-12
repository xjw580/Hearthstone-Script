package club.xiaojiawei.controller;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.DashboardUtil;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;

/**
 * @author zerg
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class JavaFXDashboardController implements Initializable {

    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private VBox logVBox;
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane titledPaneControl;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private TitledPane titledPaneLog;
    @FXML
    @Getter
    private Text gameCount;
    @FXML
    @Getter
    private Text winningPercentage;
    @FXML
    private Switch logSwitch;
    @FXML
    private ComboBox runModeBox;
    @FXML
    private ComboBox deckBox;
    @FXML
    private FlowPane workDay;
    @FXML
    private VBox workTime;
    @FXML
    private Text tip;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptProperties;
    @Resource
    private JavaFXInitSettingsController javaFXInitSettingsController;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Resource
    private Work work;
    @Resource
    private DashboardUtil dashboardUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
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
        javaFXInitSettingsController.showStage();
    }

    @FXML
    protected void save(){
//        检查挂机天
        ObservableList<Node> workDayChildren = workDay.getChildren();
        String[] workDayFlagArr = work.getWorkDayFlagArr();
        for (int i = 0; i < workDayChildren.size(); i++) {
            CheckBox workDayChild = (CheckBox) workDayChildren.get(i);
            if (Objects.equals(workDayFlagArr[i] = String.valueOf(workDayChild.isSelected()), "true") && i > 0 && Objects.equals(workDayFlagArr[0], "true")){
                workDayFlagArr[i] = "false";
                workDayChild.setSelected(false);
            }
        }
//        检查挂机段
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        String[] workTimeFlagArr = work.getWorkTimeFlagArr();
        String[] workTimeArr = work.getWorkTimeArr();
        for (int i = 0; i < workTimeChildren.size(); i++) {
            CheckBox workTimeChild = (CheckBox) workTimeChildren.get(i);
            workTimeFlagArr[i] = String.valueOf(workTimeChild.isSelected());
            TextField graphic = (TextField) workTimeChild.getGraphic();
            if (i > 0 && Strings.isNotBlank(graphic.getText())){
                if (!graphic.getText().matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}")){
                    workTimeFlagArr[i] = "false";
                    workTimeChild.setSelected(false);
                    graphic.setText("格式错误！");
                }else {
                    String[] times = graphic.getText().split("-");
                    workTimeArr[i] = graphic.getText();
                    if (times[1].compareTo(times[0]) == 0){
                        workTimeFlagArr[i] = "false";
                        workTimeChild.setSelected(false);
                        graphic.setText("不能相等！");
                    }else {
                        workTimeArr[i] = graphic.getText();
                    }
                }
            }
        }
        tip.setFill(Paint.valueOf("#00cc00"));
        tip.setText("保存成功😊");
        extraThreadPool.schedule(new LogRunnable(() -> tip.setText("")), 3, TimeUnit.SECONDS);
        Work.storeWorkDate();
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
//        初始化模式和卡组
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
//        初始化挂机时间
        String[] workDayFlagArr = work.getWorkDayFlagArr();
        ObservableList<Node> workDayChildren = workDay.getChildren();
        for (int i = 0; i < workDayFlagArr.length; i++) {
            if (Objects.equals(workDayFlagArr[i], "true")){
                CheckBox checkBox = (CheckBox) workDayChildren.get(i);
                checkBox.setSelected(true);
                if (i == 0){
                    break;
                }
            }
        }
        String[] workTimeFlagArr = work.getWorkTimeFlagArr();
        String[] workTimeArr = work.getWorkTimeArr();
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        for (int i = 0; i < workTimeFlagArr.length; i++) {
            if (!Objects.equals(workTimeArr[i], "null")){
                CheckBox checkBox = (CheckBox) workTimeChildren.get(i);
                ((TextField)checkBox.getGraphic()).setText(workTimeArr[i]);
                if (Objects.equals(workTimeFlagArr[i], "true")){
                    checkBox.setSelected(true);
                }
            }
        }
//        模式更改监听
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
//        卡组更改监听
        deckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                dashboardUtil.changeDeck((String) newValue);
            }
        });
//        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> logScrollPane.setVvalue(logScrollPane.getVmax()));
    }
    public void changeSwitch(boolean value){
        pauseButton.setDisable(value);
        startButton.setDisable(!value);
    }

}