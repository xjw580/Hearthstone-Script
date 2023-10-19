package club.xiaojiawei.custom;

import javafx.scene.control.Button;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/19 14:06
 */
public class MyButton extends Button {

    private boolean show;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
