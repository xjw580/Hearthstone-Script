package club.xiaojiawei.listener;

import club.xiaojiawei.utils.SystemUtil;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 热键监听器
 * @author 肖嘉威
 * @date 2022/12/11 11:23
 */
@Slf4j
@Configuration
public class GlobalHotkeyListener implements HotkeyListener {

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private final static int HOT_KEY_EXIT = 111;
    private final static int HOT_KEY_PAUSE = 222;

    public GlobalHotkeyListener(){
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().registerHotKey(HOT_KEY_EXIT, JIntellitype.MOD_ALT, 'P');
            JIntellitype.getInstance().registerHotKey(HOT_KEY_PAUSE, JIntellitype.MOD_CONTROL, 'P');
            JIntellitype.getInstance().addHotKeyListener(this);
        }else {
            log.warn("当前系统不支持设置热键");
        }
    }

    /**
     * 快捷键组合键按键事件
     * @param i
     */
    @Override
    public void onHotKey(int i) {
        if(i == HOT_KEY_EXIT){
            JIntellitype.getInstance().removeHotKeyListener(this);
            log.info("捕捉到热键，关闭程序");
            SystemUtil.notice("捕捉到热键，关闭程序");
            SystemUtil.shutdown();
        }else if (i == HOT_KEY_PAUSE){
            if (!isPause.get().get()){
                log.info("捕捉到热键,停止脚本");
                SystemUtil.notice("捕捉到热键,停止脚本");
                isPause.get().set(true);
            }else {
                log.info("捕捉到热键,开始脚本");
                SystemUtil.notice("捕捉到热键,开始脚本");
                isPause.get().set(false);
            }
        }
    }

}