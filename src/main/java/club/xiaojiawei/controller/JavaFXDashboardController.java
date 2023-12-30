package club.xiaojiawei.controller;

import club.xiaojiawei.bean.Release;
import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.config.ThreadPoolConfig;
import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.TimeSelector;
import club.xiaojiawei.controls.ico.FlushIco;
import club.xiaojiawei.controls.ico.OKIco;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.TipUtil;
import club.xiaojiawei.utils.WindowUtil;
import club.xiaojiawei.ws.WebSocketServer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static club.xiaojiawei.data.ScriptStaticData.*;
import static club.xiaojiawei.enums.ConfigurationEnum.DECK;
import static club.xiaojiawei.enums.ConfigurationEnum.RUN_MODE;

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
@Component
@Slf4j
public class JavaFXDashboardController implements Initializable {

    @FXML private ScrollPane logScrollPane;
    @FXML private Button update;
    @FXML private Button flush;
    @FXML private FlushIco flushIco;
    @FXML private Text version;
    @FXML private VBox logVBox;
    @FXML private Accordion accordion;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
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
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private PropertiesUtil propertiesUtil;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private VersionListener versionListener;
    private static ProgressBar staticDownloadProgress;
    public void expandedLogPane(){
        accordion.setExpandedPane(titledPaneLog);
    }
    @FXML protected void start(){
        isPause.get().set(false);
    }
    @FXML protected void pause(){
        isPause.get().set(true);
    }
    @FXML protected void showSettings() {
        WindowUtil.showStage(WindowEnum.SETTINGS);
    }
    private static AtomicReference<BooleanProperty> staticIsPause;
    private static final SimpleBooleanProperty IS_UPDATING = new SimpleBooleanProperty(false);
    @FXML protected void update() {
        Release release = VersionListener.getLatestRelease();
        if (release != null && !IS_UPDATING.get()){
            IS_UPDATING.set(true);
            extraThreadPool.submit(() -> {
                if (!new File(TEMP_PATH).exists()){
                    downloadRelease(release);
                }
                Platform.runLater(() -> WindowUtil.createAlert("新版本[" + release.getTagName() + "]下载完毕", "现在更新？", event -> {
                    execUpdate();
                }, event -> IS_UPDATING.set(false), event -> IS_UPDATING.set(false), event -> IS_UPDATING.set(false)).show());
            });
        }
    }

    public static void downloadRelease(Release release){
        try (
                InputStream inputStream = new URL(String.format("https://gitee.com/zergqueen/Hearthstone-Script/releases/download/%s/%s-%s.zip", release.getTagName(), REPO_NAME, release.getTagName()))
                        .openConnection()
                        .getInputStream();
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ) {
            log.info("开始下载新版本：" + release.getTagName());
            staticDownloadProgress.setProgress(0D);
            staticDownloadProgress.setVisible(true);
            staticDownloadProgress.setManaged(true);
            ZipEntry nextEntry;
            double count = 0;
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                File entryFile = new File(TEMP_PATH + nextEntry.getName());
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
                staticDownloadProgress.setProgress(++count / 70);
            }
            staticDownloadProgress.setProgress(1D);
            log.info(release.getTagName() + "下载完毕");
        } catch (IOException e) {
            log.error("新版本下载失败", e);
        } finally {
            staticDownloadProgress.setVisible(false);
            staticDownloadProgress.setManaged(false);
            IS_UPDATING.set(false);
        }
    }
    public static void execUpdate(){
        try {
            IS_UPDATING.set(true);
            Runtime.getRuntime().exec("cmd /c start update.bat " + TEMP_DIR + " " + staticIsPause.get().get());
        } catch (IOException e) {
            log.error("执行版本更新失败", e);
        }finally {
            IS_UPDATING.set(false);
        }
    }
    @FXML protected void save(){
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
                timeCheckBox.setSelected(false);
            }
            startTime.refresh();
            endTime.refresh();
        }
        Work.storeWorkDate();
        TipUtil.show(ok, 2);
    }
    @FXML private OKIco ok;
    public static VBox staticLogVBox;
    public static Accordion staticAccordion;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        version.setText("当前版本：" + VersionListener.getCurrentVersion());
        assign();
        initModeAndDeck();
        initWorkDate();
        listen();
    }
    @Getter
    private static RunModeEnum currentRunMode;
    @Getter
    private static DeckEnum currentDeck;
    private void assign(){
        staticLogVBox = logVBox;
        staticAccordion = accordion;
        staticDownloadProgress = downloadProgress;
        staticIsPause = isPause;
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
    private void listen(){
        //        是否在更新中监听
        IS_UPDATING.addListener((observable, oldValue, newValue) -> update.setDisable(newValue));
        //        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> logScrollPane.setVvalue(logScrollPane.getVmax()));
        VersionListener.getCanUpdate().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            if (newValue){
                flush.setVisible(false);
                flush.setManaged(false);
                update.setVisible(true);
                update.setManaged(true);
            }
        });
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
    public void changeSwitch(boolean value){
        pauseButton.setDisable(value);
        startButton.setDisable(!value);
    }

    @FXML
    protected void flush(ActionEvent actionEvent) {
        RotateTransition transition = new RotateTransition(Duration.millis(1200), flushIco);
        transition.setFromAngle(0);
        transition.setToAngle(360);
        transition.setCycleCount(4);
        transition.play();
        versionListener.checkVersion();
    }
}