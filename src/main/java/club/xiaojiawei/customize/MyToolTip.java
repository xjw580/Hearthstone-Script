package club.xiaojiawei.customize;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * @author 肖嘉威
 * @email xjw580@qq.com
 * @date 2023/2/10 下午9:09
 */
public class MyToolTip extends Tooltip {

    public MyToolTip() {
        setShowDelay(Duration.millis(400));
    }
}
