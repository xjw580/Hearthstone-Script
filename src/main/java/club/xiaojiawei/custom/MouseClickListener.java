package club.xiaojiawei.custom;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:12
 * @question <a href=""/>
 */
public class MouseClickListener implements MouseListener {

    private Consumer<MouseEvent> consumer;

    public MouseClickListener(Consumer<MouseEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        consumer.accept(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
