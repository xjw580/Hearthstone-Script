package club.xiaojiawei.controller;

import club.xiaojiawei.bean.Release;
import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.ico.ClearIco;
import club.xiaojiawei.controls.ico.FlushIco;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import club.xiaojiawei.ws.WebSocketServer;
import jakarta.annotation.Resource;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static club.xiaojiawei.data.ScriptStaticData.TEMP_VERSION_DIR;
import static club.xiaojiawei.data.ScriptStaticData.TEMP_VERSION_PATH;
import static club.xiaojiawei.enums.ConfigurationEnum.DECK;
import static club.xiaojiawei.enums.ConfigurationEnum.RUN_MODE;

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
@Component
@Slf4j
public class JavaFXDashboardController implements Initializable {

    @FXML private ToggleGroup pauseToggleGroup;
    @FXML private StackPane rootPane;
    @FXML private NotificationManager notificationManger;
    @FXML private ScrollPane logScrollPane;
    @FXML private Button updateBtn;
    @FXML private Button flushBtn;
    @FXML private FlushIco flushIco;
    @FXML private Text versionText;
    @FXML private VBox logVBox;
    @FXML private Accordion accordion;
    @FXML private ToggleButton startButton;
    @FXML private ToggleButton pauseButton;
    @FXML private TitledPane titledPaneLog;
    @FXML @Getter private Text gameCount;
    @FXML @Getter private Text winningPercentage;
    @FXML @Getter private Text gameTime;
    @FXML @Getter private Text exp;
    @FXML private ComboBox<String> runModeBox;
    @FXML private ComboBox<String> deckBox;
    @FXML private TilePane workDay;
    @FXML private VBox workTime;
    @FXML private ProgressBar downloadProgress;
    @Resource private AtomicReference<BooleanProperty> isPause;
    @Resource private PropertiesUtil propertiesUtil;
    @Resource private Properties scriptConfiguration;
    @Resource private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource private VersionListener versionListener;
    @Getter private static RunModeEnum currentRunMode;
    @Getter private static DeckEnum currentDeck;
    @Getter private static VBox staticLogVBox;
    @Getter private static Accordion staticAccordion;
    private static NotificationManager staticNotificationManger;
    private static ProgressBar staticDownloadProgress;
    private static AtomicReference<BooleanProperty> staticIsPause;
    private static final SimpleBooleanProperty IS_UPDATING = new SimpleBooleanProperty(false);
    private boolean isNotHoverLog = true;

    public void expandedLogPane(){
        accordion.setExpandedPane(titledPaneLog);
    }

    public static boolean downloadRelease(Release release){
        boolean result = true;
        if (!downloadRelease(release, String.format("https://gitee.com/zergqueen/%s/releases/download/%s/%s_%s.zip", ScriptStaticData.PROJECT_NAME, release.getTagName(), ScriptStaticData.SCRIPT_NAME, release.getTagName()))){
            Platform.runLater(() -> staticNotificationManger.showInfo("更换下载源重新下载", 3));
            result = downloadRelease(release, String.format("https://github.com/xjw580/%s/releases/download/%s/%s_%s.zip", ScriptStaticData.PROJECT_NAME, release.getTagName(), ScriptStaticData.SCRIPT_NAME, release.getTagName()));
        }
        return result;
    }

    private static boolean downloadRelease(Release release, String url){
        try (
                InputStream inputStream = new URL(url)
                        .openConnection()
                        .getInputStream();
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ) {
            String startContent = "开始下载<" + release.getTagName() + ">";
            log.info(startContent);
            Platform.runLater(() -> staticNotificationManger.showInfo(startContent, 2));
            staticDownloadProgress.setProgress(0D);
            staticDownloadProgress.setVisible(true);
            staticDownloadProgress.setManaged(true);
            ZipEntry nextEntry;
            double index = 0D, count = 74D;
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                File entryFile = new File(TEMP_VERSION_PATH + nextEntry.getName());
                if (nextEntry.isDirectory()) {
                    entryFile.mkdirs();
                    log.info("created_dir：" + entryFile.getPath());
                } else {
                    new File(entryFile.getPath().substring(0, entryFile.getPath().lastIndexOf("\\"))).mkdirs();
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                        int l;
                        byte[] bytes = new byte[1024];
                        while ((l = zipInputStream.read(bytes)) != -1) {
                            bufferedOutputStream.write(bytes, 0, l);
                        }
                    }
                    log.info("downloaded_file：" + entryFile.getPath());
                }
                staticDownloadProgress.setProgress(++index / count);
            }
            staticDownloadProgress.setProgress(1D);
            String endContent = "<" + release.getTagName() + ">下载完毕";
            log.info(endContent);
            Platform.runLater(() -> staticNotificationManger.showSuccess(endContent, 2));
        } catch (IOException e) {
            String errorContent = "<" + release.getTagName() + ">下载失败";
            log.error(errorContent + "," + url, e);
            Platform.runLater(() -> staticNotificationManger.showError(errorContent, 2));
            return false;
        } finally {
            staticDownloadProgress.setVisible(false);
            staticDownloadProgress.setManaged(false);
            IS_UPDATING.set(false);
        }
        return true;
    }

    public static void execUpdate(){
        try {
            IS_UPDATING.set(true);
            Runtime.getRuntime().exec("cmd /c start update.bat " + TEMP_VERSION_DIR + " " + staticIsPause.get().get());
        } catch (IOException e) {
            log.error("执行版本更新失败", e);
        }finally {
            IS_UPDATING.set(false);
        }
    }

    private void assign(){
        staticLogVBox = logVBox;
        staticAccordion = accordion;
        staticDownloadProgress = downloadProgress;
        staticIsPause = isPause;
        staticNotificationManger = notificationManger;
    }
    /**
     * 初始化模式和卡组
     */
    private void initModeAndDeck(){
        currentDeck = DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey()));
        currentRunMode = currentDeck.getRunMode();
        ObservableList<String> runModeBoxItems = runModeBox.getItems();
        ObservableList<String> deckBoxItems = deckBox.getItems();
        runModeBoxItems.clear();
        deckBoxItems.clear();
        RunModeEnum[] values = RunModeEnum.values();
        DeckEnum[] deckEnums = DeckEnum.values();
//        初始化模式和卡组
        for (RunModeEnum runMode : values) {
            if (runMode.isEnable()){
                runModeBoxItems.add(runMode.getComment());
            }
            if (currentRunMode == runMode){
                runModeBox.getSelectionModel().select(currentRunMode.getComment());
                addDeck(deckBoxItems, deckEnums);
            }
        }
        deckBox.getSelectionModel().select(currentDeck.getComment());
        //        模式更改监听
        runModeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for (RunModeEnum runMode : RunModeEnum.values()) {
                if (Objects.equals(runMode.getComment(), newValue)) {
                    currentRunMode = runMode;
                }
            }
            deckBoxItems.clear();
            addDeck(deckBoxItems, deckEnums);
        });
        //        卡组更改监听
        deckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                storeDeck((String) newValue);
            }
        });
    }

    private void addDeck(ObservableList<String> deckBoxItems, DeckEnum[] deckEnums){
        for (DeckEnum deck : deckEnums) {
            if (deck.getRunMode() == currentRunMode && deck.isEnable()) {
                deckBoxItems.add(deck.getComment());
            }
        }
    }

    private void storeDeck(String deckComment){
        if (!Objects.equals(DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey())).getComment(), deckComment)){
            scriptConfiguration.setProperty(RUN_MODE.getKey(), currentRunMode.name());
            for (DeckEnum anEnum : DeckEnum.values()) {
                if (Objects.equals(deckComment, anEnum.getComment())){
                    scriptConfiguration.setProperty(DECK.getKey(), (currentDeck = anEnum).name());
                    break;
                }
            }
            propertiesUtil.storeScriptProperties();
            WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.MODE, currentRunMode.getComment()));
            WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.DECK, currentDeck.getComment()));
            SystemUtil.notice("挂机卡组改为：" + deckComment);
            SystemUtil.copyToClipboard(currentDeck.getDeckCode());
            log.info("挂机卡组改为：" + deckComment);
        }
    }

    private void addListener(){
        //        是否在更新中监听
        IS_UPDATING.addListener((observable, oldValue, newValue) -> updateBtn.setDisable(newValue));
        //        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (isNotHoverLog){
                logScrollPane.setVvalue(logScrollPane.getVmax());
            }
        });
        VersionListener.getCanUpdate().addListener((observable, oldValue, newValue) -> {
            flushBtn.setVisible(!newValue);
            flushBtn.setManaged(!newValue);
            updateBtn.setVisible(newValue);
            updateBtn.setManaged(newValue);
        });
        Popup menuPopup = createMenuPopup();
        logScrollPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && !logVBox.getChildren().isEmpty()){
                menuPopup.setAnchorX(event.getScreenX() - 5);
                menuPopup.setAnchorY(event.getScreenY() - 5);
                menuPopup.show(rootPane.getScene().getWindow());
            }
        });
        String btnPressedStyleClass = "btnPressed";
        pauseToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1 == null){
                if (toggle != null){
                    pauseToggleGroup.selectToggle(toggle);
                }
            }else {
                startButton.getStyleClass().remove(btnPressedStyleClass);
                pauseButton.getStyleClass().remove(btnPressedStyleClass);
                if (t1 == startButton){
                    startButton.getStyleClass().add(btnPressedStyleClass);
                    isPause.get().set(false);
                }else if (t1 == pauseButton){
                    pauseButton.getStyleClass().add(btnPressedStyleClass);
                    isPause.get().set(true);
                }
            }
        });
    }

    private Popup createMenuPopup(){
        Popup popup = new Popup();

        Label label = new Label("清空");
        label.setOnMouseClicked(event1 -> {
            logVBox.getChildren().clear();
            popup.hide();
        });
        label.setStyle("-fx-padding: 5 10 5 10");
        label.setGraphic(new ClearIco());
        label.getStyleClass().addAll("bg-hover-ui", "radius-ui");

        VBox vBox = new VBox(label){{
            setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3);-fx-padding: 5 3 5 3;-fx-background-color: white");
        }};
        vBox.getStyleClass().add("radius-ui");

        popup.setAutoHide(true);
        popup.getContent().add(vBox);
        return popup;
    }

    /**
     * 初始化挂机时间
     */
    public void initWorkDate(){
//        初始化挂机天
        String[] workDayFlagArr = Work.getWorkDayFlagArr();
        ObservableList<Node> workDayChildren = workDay.getChildren();
        for (int i = 0; i < workDayFlagArr.length; i++) {
            ((CheckBox) workDayChildren.get(i)).setSelected(Objects.equals(workDayFlagArr[i], "true"));
        }
//        初始化挂机段
        String[] workTimeFlagArr = Work.getWorkTimeFlagArr();
        String[] workTimeArr = Work.getWorkTimeArr();
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        for (int i = 0; i < workTimeFlagArr.length; i++) {
            HBox timeHBox = (HBox) workTimeChildren.get(i);
            ObservableList<Node> timeControls = ((HBox) timeHBox.getChildren().get(1)).getChildren();
            if (workTimeArr[i] != null && !Objects.equals(workTimeArr[i], "null") && !workTimeArr[i].isBlank()){
                String[] times = workTimeArr[i].split("-");
                ((Time)timeControls.get(0)).setTime(times[0]);
                ((Time)timeControls.get(2)).setTime(times[1]);
                ((CheckBox)timeHBox.getChildren().get(0)).setSelected(Objects.equals(workTimeFlagArr[i], "true"));
            }else {
                ((Time)timeControls.get(0)).setTime(null);
                ((Time)timeControls.get(2)).setTime(null);
                ((CheckBox)timeHBox.getChildren().get(0)).setSelected(false);
            }
        }
    }
    public void changeDeck(String deckComment){
        for (DeckEnum value : DeckEnum.values()) {
            if (Objects.equals(value.getComment(), deckComment)){
                currentDeck = value;
            }
        }
        currentRunMode = currentDeck.getRunMode();
        Platform.runLater(() -> {
            runModeBox.getSelectionModel().select(currentRunMode.getComment());
            deckBox.getSelectionModel().select(currentDeck.getComment());
        });
    }
    public void changeSwitch(boolean isPause){
        if (isPause){
            pauseToggleGroup.selectToggle(pauseButton);
        }else {
            pauseToggleGroup.selectToggle(startButton);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionText.setText("当前版本：" + VersionListener.getCurrentVersion());
        assign();
        initModeAndDeck();
        initWorkDate();
        addListener();
    }

    @FXML protected void flushVersion() {
        RotateTransition transition = new RotateTransition(Duration.millis(1200), flushIco);
        transition.setFromAngle(0);
        transition.setToAngle(360);
        transition.setCycleCount(4);
        transition.play();
        versionListener.checkVersion();
    }

    @FXML protected void openSettings() {
        WindowUtil.showStage(WindowEnum.SETTINGS);
    }

    @FXML protected void updateVersion() {
        Release release = VersionListener.getLatestRelease();
        if (release != null && !IS_UPDATING.get()){
            IS_UPDATING.set(true);
            extraThreadPool.submit(() -> {
                if (!new File(TEMP_VERSION_PATH).exists()){
                    if (!downloadRelease(release)){
                        Platform.runLater(() -> WindowUtil.createAlert(String.format("新版本<%s>下载失败", release.getTagName()), "", null).show());
                        return;
                    }
                }
                Platform.runLater(() -> WindowUtil.createAlert("新版本[" + release.getTagName() + "]下载完毕", "现在更新？", event -> {
                    execUpdate();
                }, event -> IS_UPDATING.set(false), event -> IS_UPDATING.set(false), event -> IS_UPDATING.set(false)).show());
            });
        }
    }

    @FXML protected void saveTime(){
//        检查挂机天
        ObservableList<Node> workDayChildren = workDay.getChildren();
        String[] workDayFlagArr = Work.getWorkDayFlagArr();
        for (int i = 0; i < workDayChildren.size(); i++) {
            workDayFlagArr[i] = String.valueOf(((CheckBox) workDayChildren.get(i)).isSelected());
        }
//        检查挂机段
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        String[] workTimeFlagArr = Work.getWorkTimeFlagArr();
        String[] workTimeArr = Work.getWorkTimeArr();
        for (int i = 0; i < workTimeChildren.size(); i++) {
            HBox hBox = (HBox) workTimeChildren.get(i);
            ObservableList<Node> children = ((HBox) hBox.getChildren().get(1)).getChildren();
            Time startTime = (Time) children.get(0), endTime = (Time) children.get(2);
            CheckBox timeCheckBox = (CheckBox) hBox.getChildren().get(0);
            if (startTime.timeProperty().get() != null && endTime.timeProperty().get() != null){
                workTimeArr[i] = String.join("-", startTime.getTime(), endTime.getTime());
                workTimeFlagArr[i] = String.valueOf(timeCheckBox.isSelected());
            }else {
                workTimeArr[i] = "null";
                startTime.setTime(null);
                endTime.setTime(null);
                workTimeFlagArr[i] = String.valueOf(false);
                timeCheckBox.setSelected(false);
            }
            startTime.refresh();
            endTime.refresh();
        }
        Work.storeWorkDate();
        notificationManger.showSuccess("工作时间保存成功", 2);
    }

    @FXML protected void mouseEnteredLog(){
        isNotHoverLog = false;
    }
    @FXML protected void mouseExitedLog(){
        isNotHoverLog = true;
    }
}