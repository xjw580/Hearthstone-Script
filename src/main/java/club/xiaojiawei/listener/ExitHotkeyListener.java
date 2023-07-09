package club.xiaojiawei.listener;

import club.xiaojiawei.utils.SystemUtil;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2022/12/11 11:23
 */
@Slf4j
@Configuration
public class ExitHotkeyListener implements HotkeyListener {

    @Resource
    private PauseHotkeyListener pauseHotkeyListener;
    @Resource
    private SystemUtil systemUtil;

    private final static int HOT_KEY_EXIT = 111;

    public ExitHotkeyListener(){
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().registerHotKey(HOT_KEY_EXIT, JIntellitype.MOD_ALT, 'P');
            JIntellitype.getInstance().addHotKeyListener(this);
        }
    }

    /**
     * 快捷键组合键按键事件
     * @param i
     */
    @Override
    public void onHotKey(int i) {
        //如果是我指定的快捷键就执行指定的操作
        if(i == HOT_KEY_EXIT){
            log.info("捕捉到热键，关闭程序");
            systemUtil.notice("捕捉到热键，关闭程序");
            JIntellitype.getInstance().removeHotKeyListener(this);
            JIntellitype.getInstance().removeHotKeyListener(pauseHotkeyListener);
            System.exit(0);
        }
    }

}