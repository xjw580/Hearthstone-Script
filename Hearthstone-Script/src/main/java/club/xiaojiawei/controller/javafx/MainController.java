package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.Release;
import club.xiaojiawei.bean.single.repository.GiteeRepository;
import club.xiaojiawei.bean.single.repository.GithubRepository;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.ico.*;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.status.DeckStrategyManager;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static club.xiaojiawei.data.ScriptStaticData.TEMP_VERSION_PATH;
import static club.xiaojiawei.enums.ConfigurationEnum.DEFAULT_DECK_STRATEGY;

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
@Component
@Slf4j
public class MainController implements Initializable {

    @FXML
    private StartIco startIco;
    @FXML
    private PauseIco pauseIco;
    @FXML
    private ToggleGroup pauseToggleGroup;
    @FXML
    private StackPane rootPane;
    @FXML
    @Getter
    private NotificationManager<Object> notificationManger;
    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private Button updateBtn;
    @FXML
    private Button flushBtn;
    @FXML
    private FlushIco flushIco;
    @FXML
    private Text versionText;
    @FXML
    private VBox logVBox;
    @FXML
    private Accordion accordion;
    @FXML
    private ToggleButton startButton;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private TitledPane titledPaneLog;
    @FXML
    @Getter
    private Text gameCount;
    @FXML
    @Getter
    private Text winningPercentage;
    @FXML
    @Getter
    private Text gameTime;
    @FXML
    @Getter
    private Text exp;
    @FXML
    private ComboBox<RunModeEnum> runModeBox;
    @FXML
    private ComboBox<DeckStrategy> deckBox;
    @FXML
    private TilePane workDay;
    @FXML
    private VBox workTime;
    @FXML
    private ProgressBar downloadProgress;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private PropertiesUtil propertiesUtil;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private VersionListener versionListener;
    @Getter
    private static VBox staticLogVBox;
    @Getter
    private static Accordion staticAccordion;
    @Getter
    private static NotificationManager<Object> staticNotificationManger;
    private static ProgressBar staticDownloadProgress;
    private static AtomicReference<BooleanProperty> staticIsPause;
    private static final SimpleBooleanProperty UPDATING = new SimpleBooleanProperty(false);
    private static final String VERSION_FILE_FLAG_NAME = "downloaded.flag";
    private boolean isNotHoverLog = true;
    @FXML
    private TitledPane titledPaneControl;

    public void expandedLogPane() {
        accordion.setExpandedPane(titledPaneLog);
    }

    public static void downloadRelease(Release release, boolean force, Consumer<String> callback) {
        if (UPDATING.get()) {
            return;
        }
        UPDATING.set(true);
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit(() -> {
            String path = null;
            try {
                File file = Path.of(TEMP_VERSION_PATH, release.getTagName(), VERSION_FILE_FLAG_NAME).toFile();
                if (!force && file.exists()) {
                    path = file.getParentFile().getAbsolutePath();
                } else if ((path = downloadRelease(release, GiteeRepository.getInstance().getReleaseURL(release))) == null) {
                    Platform.runLater(() -> staticNotificationManger.showInfo("更换下载源重新下载", 3));
                    path = downloadRelease(release, GithubRepository.getInstance().getReleaseURL(release));
                }
            } finally {
                UPDATING.set(false);
                if (callback != null) {
                    callback.accept(path);
                }
            }
        });
    }

    private static String downloadRelease(Release release, String url) {
        Path rootPath;
        try (
                InputStream inputStream = new URI(url)
                        .toURL()
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
            rootPath = Path.of(TEMP_VERSION_PATH, release.getTagName());
            File rootFile = rootPath.toFile();
            if (!rootFile.exists() && !rootFile.mkdirs()) {
                log.error(rootFile.getAbsolutePath() + "创建失败");
                return null;
            }
            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                File entryFile = rootPath.resolve(nextEntry.getName()).toFile();
                if (nextEntry.isDirectory()) {
                    if (entryFile.mkdirs()) {
                        log.info("created_dir：" + entryFile.getPath());
                    }
                } else {
                    File parentFile = entryFile.getParentFile();
                    if (parentFile.exists() || parentFile.mkdirs()) {
                        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                            int l;
                            byte[] bytes = new byte[8192];
                            while ((l = zipInputStream.read(bytes)) != -1) {
                                bufferedOutputStream.write(bytes, 0, l);
                            }
                        }
                        log.info("downloaded_file：" + entryFile.getPath());
                    }
                }
                staticDownloadProgress.setProgress(++index / count);
            }
            writeVersionFileCompleteFlag(rootPath.toString());
            staticDownloadProgress.setProgress(1D);
            String endContent = "<" + release.getTagName() + ">下载完毕";
            log.info(endContent);
            Platform.runLater(() -> staticNotificationManger.showSuccess(endContent, 2));
        } catch (IOException | URISyntaxException e) {
            String errorContent = "<" + release.getTagName() + ">下载失败";
            log.error(errorContent + "," + url, e);
            Platform.runLater(() -> staticNotificationManger.showError(errorContent, 2));
            return null;
        } finally {
            staticDownloadProgress.setVisible(false);
            staticDownloadProgress.setManaged(false);
        }
        return rootPath.toString();
    }

    private static boolean writeVersionFileCompleteFlag(String path) {
        try {
            return Path.of(path, VERSION_FILE_FLAG_NAME).toFile().createNewFile();
        } catch (IOException e) {
            log.error("", e);
        }
        return false;
    }

    public static void execUpdate(String versionPath) {
        try {
            UPDATING.set(true);
            String rootPath = System.getProperty("user.dir");
            String updateProgramPath = rootPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME;
            Files.copy(
                    Path.of(versionPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME),
                    Path.of(rootPath + File.separator + ScriptStaticData.UPDATE_PROGRAM_NAME),
                    StandardCopyOption.REPLACE_EXISTING
            );
            Runtime.getRuntime().exec(String.format(
                    "%s --target='%s' --source='%s' --pause='%s' --pid='%s'",
                    updateProgramPath,
                    rootPath,
                    versionPath,
                    staticIsPause.get().get(),
                    ProcessHandle.current().pid()
            ));
        } catch (IOException e) {
            log.error("执行版本更新失败", e);
        } finally {
            UPDATING.set(false);
        }
    }

    private void assign() {
        staticLogVBox = logVBox;
        staticAccordion = accordion;
        staticDownloadProgress = downloadProgress;
        staticIsPause = isPause;
        staticNotificationManger = notificationManger;
    }

    private Map<RunModeEnum, List<DeckStrategy>> runModeMap = new HashMap<>();

    /**
     * 初始化模式和卡组
     */
    private void initModeAndDeck() {
        runModeBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RunModeEnum runModeEnum) {
                return runModeEnum == null ? "" : runModeEnum.getComment();
            }

            @Override
            public RunModeEnum fromString(String s) {
                return (s == null || s.isBlank()) ? null : RunModeEnum.valueOf(s);
            }
        });
        deckBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(DeckStrategy deckStrategy) {
                return deckStrategy == null ? "" : deckStrategy.name();
            }

            @Override
            public DeckStrategy fromString(String s) {
                return null;
            }
        });

        reloadRunMode();

//        模式更改监听
        runModeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deckBox.getSelectionModel().select(null);
            if (newValue == null){
                deckBox.getItems().clear();
            }else {
                deckBox.getItems().setAll(runModeMap.get(newValue));
            }
        });

//        卡组更改监听
        deckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
//                将卡组策略的第一个运行模式改为当前运行模式
                for (int i = 0; i < newValue.getRunModes().length; i++) {
                    RunModeEnum runModeEnum = newValue.getRunModes()[i];
                    if (Objects.equals(runModeEnum, runModeBox.getValue())) {
                        newValue.getRunModes()[i] = newValue.getRunModes()[0];
                        newValue.getRunModes()[0] = runModeEnum;
                        break;
                    }
                }
            }
            DeckStrategyManager.CURRENT_DECK_STRATEGY.set(newValue);
        });

        Optional<DeckStrategy> defaultDeck = DeckStrategyManager.DECK_STRATEGIES.stream().filter(deckStrategy -> Objects.equals(scriptConfiguration.getProperty(DEFAULT_DECK_STRATEGY.getKey()), deckStrategy.id())).findFirst();
        if (defaultDeck.isPresent()) {
            defaultDeck.get().getRunModes();
            runModeBox.setValue(defaultDeck.get().getRunModes()[0]);
            deckBox.setValue(defaultDeck.get());
        }

        DeckStrategyManager.CURRENT_DECK_STRATEGY.addListener((observableValue, deck, t1) -> {
            if (t1 != null) {
                t1.getRunModes();
                runModeBox.setValue(t1.getRunModes()[0]);
                deckBox.setValue(t1);
            }
        });
    }

    public void reloadRunMode() {
        runModeMap.clear();
        for (DeckStrategy deckStrategy : DeckStrategyManager.DECK_STRATEGIES) {
            for (RunModeEnum runModeEnum : deckStrategy.getRunModes()) {
                List<DeckStrategy> strategies = runModeMap.getOrDefault(runModeEnum, new ArrayList<>());
                strategies.add(deckStrategy);
                runModeMap.put(runModeEnum, strategies);
            }
        }
        runModeBox.getItems().setAll(runModeMap.keySet());
    }


    private void addListener() {
        DeckStrategyManager.DECK_STRATEGIES.addListener((SetChangeListener<? super DeckStrategy>) observable -> {
            reloadRunMode();
        });
        //        是否在更新中监听
        UPDATING.addListener((observable, oldValue, newValue) -> updateBtn.setDisable(newValue));
        //        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (isNotHoverLog) {
                logScrollPane.setVvalue(logScrollPane.getVmax());
            }
        });
        VersionListener.canUpdateReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
            flushBtn.setVisible(!newValue);
            flushBtn.setManaged(!newValue);
            updateBtn.setVisible(newValue);
            updateBtn.setManaged(newValue);
        });
        String btnPressedStyleClass = "btnPressed";
        pauseToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1 == null) {
                if (toggle != null) {
                    pauseToggleGroup.selectToggle(toggle);
                }
            } else {
                startButton.getStyleClass().remove(btnPressedStyleClass);
                pauseButton.getStyleClass().remove(btnPressedStyleClass);
                if (t1 == startButton) {
                    startIco.setColor("gray");
                    pauseIco.setColor("black");
                    startButton.getStyleClass().add(btnPressedStyleClass);
                    isPause.get().set(false);
                } else if (t1 == pauseButton) {
                    pauseIco.setColor("gray");
                    startIco.setColor("black");
                    pauseButton.getStyleClass().add(btnPressedStyleClass);
                    AbstractIco graphic = (AbstractIco) pauseButton.getGraphic();
                    graphic.setColor("gray");
                    isPause.get().set(true);
                }
            }
        });
    }

    private Popup createMenuPopup() {
        Popup popup = new Popup();

        Label label = new Label("清空");
        label.setOnMouseClicked(event1 -> {
            logVBox.getChildren().clear();
            popup.hide();
        });
        label.setStyle("-fx-padding: 5 10 5 10");
        label.setGraphic(new ClearIco());
        label.getStyleClass().addAll("bg-hover-ui", "radius-ui");

        VBox vBox = new VBox(label) {{
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
    public void initWorkDate() {
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
            if (workTimeArr[i] != null && !Objects.equals(workTimeArr[i], "null") && !workTimeArr[i].isBlank()) {
                String[] times = workTimeArr[i].split("-");
                ((Time) timeControls.get(0)).setTime(times[0]);
                ((Time) timeControls.get(2)).setTime(times[1]);
                ((CheckBox) timeHBox.getChildren().get(0)).setSelected(Objects.equals(workTimeFlagArr[i], "true"));
            } else {
                ((Time) timeControls.get(0)).setTime(null);
                ((Time) timeControls.get(2)).setTime(null);
                ((CheckBox) timeHBox.getChildren().get(0)).setSelected(false);
            }
        }
    }

    public void changeSwitch(boolean isPause) {
        if (isPause) {
            pauseToggleGroup.selectToggle(pauseButton);
        } else {
            pauseToggleGroup.selectToggle(startButton);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionText.setText("当前版本：" + VersionListener.getCurrentRelease().getTagName());
        assign();
        initModeAndDeck();
        initWorkDate();
        addListener();
    }

    @FXML
    protected void flushVersion() {
        RotateTransition transition = new RotateTransition(Duration.millis(1200), flushIco);
        transition.setFromAngle(0);
        transition.setToAngle(360);
        transition.setCycleCount(Timeline.INDEFINITE);
        try {
            transition.play();
            versionListener.checkVersion();
            if (VersionListener.isCanUpdate()) {
                notificationManger.showSuccess("发现新版本", 2);
            } else {
                notificationManger.showInfo("已是最新版本", 2);
            }
        } finally {
            transition.stop();
        }
    }

    @FXML
    protected void openSettings() {
        Stage stage = WindowUtil.buildStage(WindowEnum.SETTINGS);
        if (stage.getOwner() == null){
            stage.initOwner(WindowUtil.getStage(WindowEnum.MAIN));
        }
        stage.show();
    }

    @FXML
    protected void updateVersion() {
        Release release = VersionListener.getLatestRelease();
        if (release != null) {
            downloadRelease(release, false, path -> {
                if (path == null) {
                    Platform.runLater(() -> WindowUtil.createAlert(String.format("新版本<%s>下载失败", release.getTagName()), "", rootPane.getScene().getWindow()).show());
                } else {
                    Platform.runLater(() -> WindowUtil.createAlert("新版本[" + release.getTagName() + "]下载完毕", "现在更新？", event -> execUpdate(path), event -> UPDATING.set(false), rootPane.getScene().getWindow()).show());
                }
            });
        }
    }

    @FXML
    protected void saveTime() {
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
            if (startTime.timeProperty().get() != null && endTime.timeProperty().get() != null) {
                workTimeArr[i] = String.join("-", startTime.getTime(), endTime.getTime());
                workTimeFlagArr[i] = String.valueOf(timeCheckBox.isSelected());
            } else {
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

    @FXML
    protected void mouseEnteredLog() {
        isNotHoverLog = false;
    }

    @FXML
    protected void mouseExitedLog() {
        isNotHoverLog = true;
    }

    @FXML
    protected void mouseClickedLog(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY && !logVBox.getChildren().isEmpty()) {
            Popup menuPopup = createMenuPopup();
            menuPopup.setAnchorX(event.getScreenX() - 5);
            menuPopup.setAnchorY(event.getScreenY() - 5);
            menuPopup.show(rootPane.getScene().getWindow());
        }
    }
}