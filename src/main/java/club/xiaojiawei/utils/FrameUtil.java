package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.StageEnum;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 窗口工具类
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 */
@Component
public class FrameUtil {

    private static ApplicationContext context;

    @Resource
    public void setContext(ApplicationContext context) {
        FrameUtil.context = context;
    }

    /**
     * 创建永久置顶的窗口
     * @param frameTitle
     * @param scene
     * @param frameWidth
     * @param frameHeight
     * @return
     */
    public static AtomicReference<JFrame> createAlwaysTopWindowFrame(String frameTitle, Scene scene, int frameWidth, int frameHeight){
        AtomicReference<JFrame> atomFrame = new AtomicReference<>();
//        异步，所以用返回原子类
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame(frameTitle);
                frame.setIconImage(new ImageIcon(Objects.requireNonNull(FrameUtil.class.getResourceAsStream(ScriptStaticData.SCRIPT_ICON_PATH)).readAllBytes()).getImage());
                frame.setSize(frameWidth, frameHeight);
                Rectangle2D bounds = Screen.getPrimary().getBounds();
                frame.setLocation((int) (bounds.getWidth() - frameWidth + 5), (int) (bounds.getHeight() - frameHeight) >> 1);
                final JFXPanel fxPanel = new JFXPanel();
                frame.add(fxPanel);
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
                Platform.runLater(() -> fxPanel.setScene(scene));
                atomFrame.set(frame);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return atomFrame;
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

    private final static Map<StageEnum, Stage> STAGE_MAP = new HashMap<>();
    public static void showStage(StageEnum stageEnum){
        Stage stage = getStage(stageEnum);
        if (stage.isShowing()){
            stage.requestFocus();
        }else {
            stage.show();
        }
    }
    public static void hideStage(StageEnum stageEnum){
        Stage stage = getStage(stageEnum, false);
        if (stage != null && stage.isShowing()){
            stage.hide();
        }
    }

    public static Stage getStage(StageEnum stageEnum){
        return getStage(stageEnum, true);
    }
    public static Stage getStage(StageEnum stageEnum, boolean createStage){
        Stage stage = STAGE_MAP.get(stageEnum);
        if (stage == null && createStage){
            stage = createStage(stageEnum);
            STAGE_MAP.put(stageEnum, stage);
        }
        return stage;
    }
    private static Stage createStage(StageEnum stageEnum){
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FrameUtil.class.getResource(ScriptStaticData.MAIN_PATH + stageEnum.getFxmlName()));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setTitle(stageEnum.getTitle());
            stage.getIcons().add(new Image(Objects.requireNonNull(FrameUtil.class.getResource(ScriptStaticData.SCRIPT_ICON_PATH)).toExternalForm()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stage;
    }
}
