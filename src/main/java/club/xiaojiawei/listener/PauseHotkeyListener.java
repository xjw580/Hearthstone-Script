package club.xiaojiawei.listener;

import club.xiaojiawei.run.Core;
import club.xiaojiawei.utils.SystemUtil;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author 肖嘉威
 * @date 2022/12/11 14:11
 */
@Slf4j
@Configuration
public class PauseHotkeyListener implements HotkeyListener {
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
            if (Core.getPause()){
                SystemUtil.notice("当前正在暂停");
                return;
            }
            log.info("捕捉到热键,暂停程序");
            Core.setPause(true);
            SystemUtil.notice("捕捉到热键,暂停程序");
        }
    }

}