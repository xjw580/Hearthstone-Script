package club.xiaojiawei.utils;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.WindowEnum;
import jakarta.annotation.Resource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 窗口工具类
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 */
@Component
public class WindowUtil {

    private static ApplicationContext context;

    private final static Map<WindowEnum, Stage> STAGE_MAP = new HashMap<>();

    @Resource
    public void setContext(ApplicationContext context) {
        WindowUtil.context = context;
    }

    public static Popup createMenuPopup(Label... labels){
        Popup popup = new Popup();

        VBox vBox = new VBox(){{
            setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3);-fx-padding: 5 3 5 3;-fx-background-color: white");
        }};
        vBox.getStyleClass().add("radius-ui");

        popup.setAutoHide(true);
        popup.getContent().add(vBox);
        return popup;
    }

    /**
     * 创建对话框
     * @param headerText
     * @param contentText
     * @param okHandler
     * @param cancelHandler
     * @return
     */
    public static Stage createAlert(
            String headerText,
            String contentText,
            EventHandler<ActionEvent> okHandler,
            EventHandler<ActionEvent> cancelHandler,
            Window window
    ){
        Stage stage = new Stage();
        VBox rootPane = new VBox();
        rootPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 0);-fx-background-radius: 5;-fx-background-insets: 10;-fx-padding: 10");
        Button okBtn = new javafx.scene.control.Button("确认");
        okBtn.getStyleClass().addAll("btn-ui", "btn-ui-success");
        okBtn.setOnAction(actionEvent -> {
            stage.hide();
            if (okHandler != null) {
                okHandler.handle(actionEvent);
            }
        });
        Button cancelBtn = new javafx.scene.control.Button("取消");
        cancelBtn.getStyleClass().addAll("btn-ui");
        cancelBtn.setOnAction(actionEvent -> {
            stage.hide();
            if (cancelHandler != null) {
                cancelHandler.handle(actionEvent);
            }
        });
        HBox head = new HBox(new Label(headerText){{setStyle("-fx-wrap-text: true");}});
        HBox center = new HBox(new Label(contentText){{setStyle("-fx-wrap-text: true");}});
        HBox bottom = new HBox(okBtn, cancelBtn);
        head.setAlignment(Pos.CENTER_LEFT);
        center.setAlignment(Pos.CENTER_LEFT);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        head.setStyle("-fx-padding: 15;-fx-font-weight: bold");
        center.setStyle("-fx-padding: 10 30 10 30;-fx-font-size: 14");
        bottom.setStyle("-fx-padding: 10;-fx-spacing: 20");
        rootPane.getChildren().addAll(head, center, bottom);
        Scene scene = new Scene(rootPane, 400, -1);
        scene.setFill(Paint.valueOf("#FFFFFF00"));
        JavaFXUI.addjavafxUIStylesheet(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(WindowUtil.class.getResource(ScriptStaticData.SCRIPT_ICON_PATH)).toExternalForm()));
        stage.showingProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1 && cancelHandler != null) {
                cancelHandler.handle(null);
            }
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(window);
        return stage;
    }

    public static Stage createAlert(String headerText, String contentText, Window window){
        return createAlert(headerText, contentText, null, null, window);
    }


    public static void showStage(WindowEnum windowEnum){
        Stage stage = buildStage(windowEnum);
        if (stage.isShowing()){
            SystemUtil.frontWindow(SystemUtil.findHWND(windowEnum.getTitle()));
            stage.requestFocus();
        }else {
            stage.show();
        }
    }
    public static void hideStage(WindowEnum windowEnum){
        Stage stage = buildStage(windowEnum, false);
        if (stage != null && stage.isShowing()){
            stage.hide();
        }
    }

    public static void hideAllStage(){
        for (WindowEnum value : WindowEnum.values()) {
            hideStage(value);
        }
    }
    public static Stage buildStage(WindowEnum windowEnum){
        return buildStage(windowEnum, true);
    }
    public static Stage buildStage(WindowEnum windowEnum, boolean createStage){
        Stage stage = STAGE_MAP.get(windowEnum);
        if (stage == null && createStage){
            STAGE_MAP.put(windowEnum, stage = createStage(windowEnum));
        }
        return stage;
    }
    private static Stage createStage(WindowEnum windowEnum){
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowUtil.class.getResource(ScriptStaticData.FXML_PATH + windowEnum.getFxmlName()));
            if (context != null){
                fxmlLoader.setControllerFactory(context::getBean);
            }
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(JavaFXUI.javafxUIStylesheet());
            stage.setScene(scene);
            stage.setTitle(windowEnum.getTitle());
            stage.getIcons().add(new Image(Objects.requireNonNull(WindowUtil.class.getResource(ScriptStaticData.SCRIPT_ICON_PATH)).toExternalForm()));
            stage.setWidth(windowEnum.getWidth());
            stage.setHeight(windowEnum.getHeight());
            stage.setMinHeight(windowEnum.getHeight());
            stage.setMinWidth(windowEnum.getWidth());
            if (windowEnum.getX() != -1){
                stage.setX(windowEnum.getX());
            }
            if (windowEnum.getY() != -1){
                stage.setY(windowEnum.getY());
            }
            stage.setAlwaysOnTop(windowEnum.isAlwaysOnTop());
            stage.initStyle(windowEnum.getInitStyle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stage;
    }

    /**
     * 获取stage
     * @param windowEnum
     * @return
     */
    public static Stage getStage(WindowEnum windowEnum){
        return STAGE_MAP.get(windowEnum);
    }
}
