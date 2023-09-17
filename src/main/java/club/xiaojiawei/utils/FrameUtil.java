package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Screen;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 * @msg
 */
public class FrameUtil {

    /**
     * 创建永久置顶的窗口
     */
    public static AtomicReference<JFrame> createAlwaysTopWindow(String frameTitle, Scene scene, int frameWidth, int frameHeight, String frameIconPath){
        AtomicReference<JFrame> atomFrame = new AtomicReference<>();
//        异步，所以用返回原子类
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame(frameTitle);
                frame.setIconImage(new ImageIcon(Objects.requireNonNull(FrameUtil.class.getResourceAsStream(frameIconPath)).readAllBytes()).getImage());
                frame.setSize(frameWidth, frameHeight);
                Rectangle2D bounds = Screen.getPrimary().getBounds();
                frame.setLocation((int) (bounds.getWidth() - frameWidth), (int) (bounds.getHeight() - frameHeight) >> 1);
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

    public static Alert createAlert(String headerText, String contentText, EventHandler<ActionEvent> ok, EventHandler<ActionEvent> cancel, EventHandler<ActionEvent> close){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ScriptStaticData.SCRIPT_NAME);
        alert.getButtonTypes().add(ButtonType.CLOSE);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        Button closeButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CLOSE);
        okButton.setOnAction(ok);
        cancelButton.setOnAction(cancel);
        closeButton.setOnAction(close);
        return alert;
    }
    public static Alert createAlert(String headerText, String contentText, EventHandler<ActionEvent> ok){
        return createAlert(headerText, contentText, ok, null, null);
    }

}
