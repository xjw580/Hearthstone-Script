package club.xiaojiawei.utils;

import club.xiaojiawei.customize.MyJFrame;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/2/10 19:42
 */
public class FrameUtil {
    /**
     * 创建永久置顶的窗口
     * @param title
     * @param scene
     * @param width
     * @param height
     */
    public static AtomicReference<JFrame> createTopWindow(String title, Scene scene, int width, int height, String imageName){
        AtomicReference<JFrame> frame = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            try {
                frame.set(initAndShowGUI(title, scene, width, height, FrameUtil.class.getResourceAsStream("/club/xiaojiawei/images/" + imageName).readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return frame;
    }

    private static JFrame initAndShowGUI(String title, Scene scene, int width, int height, byte[] imageByte) {
        MyJFrame frame = new MyJFrame(title);
        frame.setIconImage(new ImageIcon(imageByte).getImage());
        frame.setSize(width, height);
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        frame.setLocation((int) (bounds.getWidth() - width), (int) (bounds.getHeight() - height) >> 1);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        Platform.runLater(() -> fxPanel.setScene(scene));
        return frame;
    }

}
