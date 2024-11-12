package club.xiaojiawei.hsscript.controller.javafx;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.controls.CopyLabel;
import club.xiaojiawei.controls.Modal;
import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.ico.AbstractIco;
import club.xiaojiawei.controls.ico.ClearIco;
import club.xiaojiawei.controls.ico.CopyIco;
import club.xiaojiawei.controls.ico.OKIco;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.hsscript.appender.ExtraLogAppender;
import club.xiaojiawei.hsscript.bean.Release;
import club.xiaojiawei.hsscript.bean.WorkDay;
import club.xiaojiawei.hsscript.bean.WorkTime;
import club.xiaojiawei.hsscript.bean.single.WarEx;
import club.xiaojiawei.hsscript.controller.javafx.view.MainView;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.listener.VersionListener;
import club.xiaojiawei.hsscript.status.DeckStrategyManager;
import club.xiaojiawei.hsscript.status.PauseStatus;
import club.xiaojiawei.hsscript.utils.ConfigExUtil;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.SystemUtil;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 肖嘉威
 * @date 2023/2/21 12:33
 */
public class MainController extends MainView {

    //调试日志
    private final static Logger log = LoggerFactory.getLogger(MainController.class);


    private boolean isNotHoverLog = true;

    private final Map<RunModeEnum, List<DeckStrategy>> runModeMap = new HashMap<>();

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
            if (newValue == null) {
                deckBox.getItems().clear();
            } else {
                deckBox.getItems().setAll(runModeMap.get(newValue));
            }
            ConfigUtil.INSTANCE.putString(ConfigEnum.DEFAULT_RUN_MODE, newValue == null ? "" : newValue.name(), true);
        });

//        卡组更改监听
        deckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
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
            DeckStrategyManager.INSTANCE.setCurrentDeckStrategy(newValue);
        });

        String defaultDeckId = ConfigUtil.INSTANCE.getString(ConfigEnum.DEFAULT_DECK_STRATEGY);
        RunModeEnum defaultRunModeEnum = RunModeEnum.Companion.fromString(ConfigUtil.INSTANCE.getString(ConfigEnum.DEFAULT_RUN_MODE));
        Optional<DeckStrategy> defaultDeck = DeckStrategyManager.INSTANCE.getDeckStrategies()
                .stream()
                .filter(deckStrategy ->
                        Objects.equals(defaultDeckId, deckStrategy.id())
                                &&
                                deckStrategy.getRunModes().length > 0
                                &&
                                (defaultRunModeEnum == null
                                        ||
                                        Arrays.stream(deckStrategy.getRunModes()).anyMatch(runModeEnum -> runModeEnum == defaultRunModeEnum))
                )
                .findFirst();
        if (defaultDeck.isPresent()) {
            defaultDeck.get().getRunModes();
            runModeBox.setValue(Objects.requireNonNullElseGet(defaultRunModeEnum, () -> defaultDeck.get().getRunModes()[0]));
            deckBox.setValue(defaultDeck.get());
        }

        DeckStrategyManager.INSTANCE.getCurrentDeckStrategyProperty().addListener((observableValue, deck, t1) -> {
            if (t1 != null) {
                t1.getRunModes();
                runModeBox.setValue(t1.getRunModes()[0]);
                deckBox.setValue(t1);
            }
        });
    }

    public void reloadRunMode() {
        runModeMap.clear();
        for (DeckStrategy deckStrategy : DeckStrategyManager.INSTANCE.getDeckStrategies()) {
            for (RunModeEnum runModeEnum : deckStrategy.getRunModes()) {
                List<DeckStrategy> strategies = runModeMap.getOrDefault(runModeEnum, new ArrayList<>());
                strategies.add(deckStrategy);
                runModeMap.put(runModeEnum, strategies);
            }
        }
        runModeBox.getItems().setAll(runModeMap.keySet());
    }

    private static String formatTime(int time) {
        String timeStr;
        if (time == 0) {
            timeStr = String.format("%d", time);
        } else if (time < 60) {
            timeStr = String.format("%dm", time);
        } else if (time < 1440) {
            if (time % 60 == 0) {
                timeStr = String.format("%dh", time / 60);
            } else {
                timeStr = String.format("%dh%dm", time / 60, time % 60);
            }
        } else {
            if (time % 1440 == 0) {
                timeStr = String.format("%dd", time / 1440);
            } else {
                timeStr = String.format("%dd%dh", time / 1440, time % 1440 / 60);
            }
        }
        return timeStr;
    }

    @SuppressWarnings("all")
    private void appendLog(ILoggingEvent event) {
        Platform.runLater(() -> {
            ObservableList<Node> list = logVBox.getChildren();
            //                大于二百五条就清空,防止内存泄露和性能问题
            if (list.size() > 250) {
                list.clear();
            }
            CopyLabel label = new CopyLabel();
            label.setNotificationManager(notificationManger);
            label.setStyle("-fx-wrap-text: true");
            label.prefWidthProperty().bind(accordion.widthProperty().subtract(15));

            int levelInt = event.getLevel().levelInt;
            String message = event.getMessage();
//                处理需要复制的文本
            if (message != null && message.startsWith("$")) {
                message = message.substring(1);
                label.setText(message);
                label.getStyleClass().add("copyLog");
                AnchorPane anchorPane = wrapLabel(label);
                list.add(anchorPane);
                return;
            }
            /*为日志上颜色*/
            if (event.getThrowableProxy() == null && levelInt <= Level.INFO_INT) {
                label.setText(message);
            } else if (levelInt <= Level.WARN_INT) {
                label.setText(message);
                label.getStyleClass().add("warnLog");
            } else {
                label.setText(message + "，查看脚本日志获取详细错误信息");
                label.getStyleClass().add("errorLog");
            }
            list.add(label);
        });
    }

    private AnchorPane wrapLabel(Label label) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("hoverRootNode");
        Node node = buildCopyNode(() -> SystemUtil.INSTANCE.copyToClipboard(label.getText()));
        node.getStyleClass().add("hoverChildrenNode");
        anchorPane.getChildren().add(label);
        anchorPane.getChildren().add(node);
        AnchorPane.setRightAnchor(node, 5D);
        AnchorPane.setTopAnchor(node, 5D);
        return anchorPane;
    }

    private Node buildCopyNode(Runnable clickHandler) {
        Label graphicLabel = new Label();
        CopyIco copyIco = new CopyIco();
        String icoColor = "#e4e4e4";
        copyIco.setColor(icoColor);
        graphicLabel.setGraphic(copyIco);
        graphicLabel.setStyle("""
                -fx-cursor: hand;
                -fx-alignment: CENTER;
                -fx-pref-width: 22;
                -fx-pref-height: 22;
                -fx-background-radius: 3;
                -fx-background-color: rgba(128,128,128,0.9);
                -fx-font-size: 10;
                """);
        graphicLabel.setOnMouseClicked(actionEvent -> {
            if (clickHandler != null) {
                clickHandler.run();
            }
            OKIco okIco = new OKIco();
            okIco.setColor(icoColor);
            graphicLabel.setGraphic(okIco);
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
            pauseTransition.setOnFinished(actionEvent1 -> {
                graphicLabel.setGraphic(copyIco);
            });
            pauseTransition.play();
        });
        return graphicLabel;
    }


    private void addListener() {
//        日志监听
        ExtraLogAppender.addCallback(this::appendLog);

//        暂停状态监听
        PauseStatus.INSTANCE.addListener((observableValue, aBoolean, t1) -> changeSwitch(t1));

        WarEx warInstance = WarEx.INSTANCE;

//        游戏局数监听
        warInstance.getWarCountProperty().addListener((observableValue, number, t1) -> {
            log.info("已完成第 " + t1 + " 把游戏");
            gameCount.setText(String.valueOf(warInstance.getWarCount()));
            winningPercentage.setText(
                    (String.format(
                            "%.1f",
                            (double) warInstance.getWinCount() / warInstance.getWarCount() * 100D
                    ) + "%"));
            gameTime.setText(formatTime(warInstance.getHangingTime()));
            exp.setText(String.valueOf(warInstance.getHangingEXP()));
        });
        DeckStrategyManager.INSTANCE.getDeckStrategies().addListener((SetChangeListener<? super DeckStrategy>) observable -> {
            reloadRunMode();
        });
        //        是否在下载中监听
        VersionListener.INSTANCE.downloadingReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> updateBtn.setDisable(newValue));
        });
        //        监听日志自动滑到底部
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (isNotHoverLog) {
                logScrollPane.setVvalue(logScrollPane.getVmax());
            }
        });
        VersionListener.INSTANCE.canUpdateReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                flushBtn.setVisible(!newValue);
                flushBtn.setManaged(!newValue);
                updateBtn.setVisible(newValue);
                updateBtn.setManaged(newValue);
            });
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
                    PauseStatus.INSTANCE.asyncSetPause(false);
                } else if (t1 == pauseButton) {
                    pauseIco.setColor("gray");
                    startIco.setColor("black");
                    pauseButton.getStyleClass().add(btnPressedStyleClass);
                    AbstractIco graphic = (AbstractIco) pauseButton.getGraphic();
                    graphic.setColor("gray");
                    PauseStatus.INSTANCE.asyncSetPause(true);
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
        Map<String, Boolean> workDayMap = ConfigExUtil.INSTANCE.getWorkDay().stream().collect(Collectors.toMap(WorkDay::getDay, WorkDay::getEnabled));
        ;
        ObservableList<Node> workDayChildren = workDay.getChildren();
        for (Node workDayChild : workDayChildren) {
            if (workDayChild instanceof CheckBox checkBox) {
                Boolean enable = workDayMap.get(workDayChild.getUserData().toString());
                checkBox.setSelected(enable != null && enable);
            }
        }
//        初始化挂机段
        List<WorkTime> workTimes = ConfigExUtil.INSTANCE.getWorkTime();
        int length = Math.min(workTimes.size(), workDayChildren.size());
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        for (int i = 0; i < length; i++) {
            HBox timeHBox = (HBox) workTimeChildren.get(i);
            ObservableList<Node> timeControls = ((HBox) timeHBox.getChildren().get(1)).getChildren();
            WorkTime time = workTimes.get(i);
            ((Time) timeControls.get(0)).setTime(time.getStartTime());
            ((Time) timeControls.get(2)).setTime(time.getEndTime());
            ((CheckBox) timeHBox.getChildren().getFirst()).setSelected(time.getEnabled());
        }
    }

    private void changeSwitch(boolean isPause) {
        if (isPause) {
            pauseToggleGroup.selectToggle(pauseButton);
            accordion.setExpandedPane(titledPaneControl);
        } else {
            pauseToggleGroup.selectToggle(startButton);
            accordion.setExpandedPane(titledPaneLog);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionText.setText("当前版本：" + VersionListener.INSTANCE.getCurrentRelease().getTagName());
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
            VersionListener.INSTANCE.checkVersion();
            if (VersionListener.INSTANCE.getCanUpdate()) {
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
        Stage stage = WindowUtil.INSTANCE.getStage(WindowEnum.SETTINGS);
        if (stage == null) {
            stage = WindowUtil.INSTANCE.buildStage(WindowEnum.SETTINGS);
        }
        if (stage.getOwner() == null) {
            stage.initOwner(WindowUtil.INSTANCE.getStage(WindowEnum.MAIN));
        }
        stage.show();
    }

    @FXML
    protected void updateVersion() {
        if (VersionListener.INSTANCE.getCanUpdate()) {
            SimpleDoubleProperty progress = new SimpleDoubleProperty();
            Release release = VersionListener.INSTANCE.getLatestRelease();
            if (release == null) return;

            VersionListener.INSTANCE.downloadLatestRelease(false, progress, path -> {
                if (path == null) {
                    Platform.runLater(
                            () -> WindowUtil.INSTANCE.createAlert(String.format("新版本<%s>下载失败", release.getTagName()),
                                    "",
                                    rootPane.getScene().getWindow()).show()
                    );
                } else {
                    Platform.runLater(
                            () -> WindowUtil.INSTANCE.createAlert("新版本[" + release.getTagName() + "]下载完毕",
                                    "现在更新？",
                                    event -> VersionListener.INSTANCE.execUpdate(path),
                                    event -> {
                                    },
                                    rootPane.getScene().getWindow()).show()
                    );
                }
            });
        }
    }

    @FXML
    protected void saveTime() {
//        检查挂机天
        ObservableList<Node> workDayChildren = workDay.getChildren();
        ArrayList<WorkDay> workDays = new ArrayList<>();
        for (Node workDayChild : workDayChildren) {
            if (workDayChild instanceof CheckBox checkBox) {
                workDays.add(new WorkDay(checkBox.getUserData().toString(), checkBox.isSelected()));
            }
        }
        ConfigExUtil.INSTANCE.storeWorkDay(workDays);
//        检查挂机段
        ObservableList<Node> workTimeChildren = workTime.getChildren();
        ArrayList<WorkTime> workTimes = new ArrayList<>();
        for (Node workTimeChild : workTimeChildren) {
            HBox hBox = (HBox) workTimeChild;
            ObservableList<Node> children = ((HBox) hBox.getChildren().get(1)).getChildren();
            Time startTime = (Time) children.get(0), endTime = (Time) children.get(2);
            CheckBox timeCheckBox = (CheckBox) hBox.getChildren().get(0);
            startTime.refresh();
            endTime.refresh();
            WorkTime time = new WorkTime(startTime.getTime(), endTime.getTime(), timeCheckBox.isSelected());
            workTimes.add(time);
        }
        ConfigExUtil.INSTANCE.storeWorkTime(workTimes);

        initWorkDate();
        notificationManger.showSuccess("工作时间保存成功", 2);
    }

    @FXML
    protected void resetStatistics(MouseEvent event) {
        if (!Objects.equals(event.getButton(), MouseButton.PRIMARY)) return;
        Modal modal = new Modal(rootPane, null, "重置统计数据？", () -> Platform.runLater(() -> {
            WarEx.INSTANCE.resetStatistics();

            gameCount.setText("0");
            winningPercentage.setText("?");
            gameTime.setText("0");
            exp.setText("0");

            notificationManger.showSuccess("统计数据已重置", 2);
        }), ()->{});
        modal.setMaskClosable(true);
        modal.show();
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

    @FXML
    protected void openVersionMsg(MouseEvent mouseEvent) {
        WindowUtil.INSTANCE.showStage(WindowEnum.VERSION_MSG, rootPane.getScene().getWindow());
    }
}