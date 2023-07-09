package club.xiaojiawei.utils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
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

}
