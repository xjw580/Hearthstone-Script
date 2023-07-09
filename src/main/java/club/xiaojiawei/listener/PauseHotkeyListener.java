package club.xiaojiawei.listener;

import club.xiaojiawei.utils.SystemUtil;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2022/12/11 14:11
 */
@Slf4j
@Configuration
public class PauseHotkeyListener implements HotkeyListener {

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private SystemUtil systemUtil;
    private final static int HOT_KEY_PAUSE = 222;

    public PauseHotkeyListener(){
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().registerHotKey(HOT_KEY_PAUSE, JIntellitype.MOD_CONTROL, 'P');
            JIntellitype.getInstance().addHotKeyListener(this);
        }
    }

    /**
     * 快捷键组合键按键事件
     * @param i
     */
    @Override
    public void onHotKey(int i) {
        if (i == HOT_KEY_PAUSE){
            if (!isPause.get().get()){
                log.info("捕捉到热键,停止脚本");
                isPause.get().set(true);
                systemUtil.notice("捕捉到热键,停止脚本");
            }else {
                log.info("捕捉到热键,开始脚本");
                isPause.get().set(false);
                systemUtil.notice("捕捉到热键,开始脚本");
            }
        }
    }

}