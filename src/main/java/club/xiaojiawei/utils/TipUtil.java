package club.xiaojiawei.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/1 0:55
 */
@Component
public class TipUtil {

    public static ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private void setExtraThreadPool(ScheduledThreadPoolExecutor extraThreadPool){
        TipUtil.extraThreadPool = extraThreadPool;
    }

    public static void show(Node icoNode, long delaySec){
        icoNode.setManaged(true);
        icoNode.setVisible(true);
        extraThreadPool.schedule(() -> {
            icoNode.setVisible(false);
            icoNode.setManaged(false);
        }, delaySec, TimeUnit.SECONDS);
    }
    public static void show(Node icoNode){
        show(icoNode, 3);
    }
    public static void show(Label icoNode, String text, long delaySec){
        icoNode.setText(text);
        show(icoNode, delaySec);
    }
    public static void show(Label icoNode, String text){
        show(icoNode, text, 3);
    }
    public static void show(Label icoNode){
        show(icoNode, "", 3);
    }
}
