package club.xiaojiawei.utils;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.WindowEnum;
import jakarta.annotation.Resource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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

    @Resource
    public void setContext(ApplicationContext context) {
        WindowUtil.context = context;
    }

    /**
     * 创建对话框
     * @param headerText
     * @param contentText
     * @param okHandler
     * @param cancelHandler
     * @param closeHandler
     * @param windowCloseHandler
     * @return
     */
    public static Alert createAlert(
            String headerText,
            String contentText,
            EventHandler<ActionEvent> okHandler,
            EventHandler<ActionEvent> cancelHandler,
            EventHandler<ActionEvent> closeHandler,
            EventHandler<DialogEvent> windowCloseHandler
    ){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ScriptStaticData.SCRIPT_NAME);
        alert.getButtonTypes().add(ButtonType.CLOSE);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        Button closeButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        okButton.setOnAction(okHandler);
        cancelButton.setOnAction(cancelHandler);
        closeButton.setOnAction(closeHandler);
        alert.setOnCloseRequest(windowCloseHandler);
        return alert;
    }

    public static Alert createAlert(String headerText, String contentText, EventHandler<ActionEvent> okHandler){
        return createAlert(headerText, contentText, okHandler, null, null, null);
    }

    private final static Map<WindowEnum, Stage> STAGE_MAP = new HashMap<>();
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
            FXMLLoader fxmlLoader = new FXMLLoader(WindowUtil.class.getResource(ScriptStaticData.MAIN_PATH + windowEnum.getFxmlName()));
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
}
