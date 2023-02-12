package club.xiaojiawei.customize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * @author 肖嘉威
 * @date 2023/2/10 19:37
 */
public class MyJFrame extends JFrame {

    public MyJFrame() throws HeadlessException {
    }

    public MyJFrame(String title) throws HeadlessException {
        super(title);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.setVisible(false);
            return;
        }
        super.processWindowEvent(e);
    }
}
