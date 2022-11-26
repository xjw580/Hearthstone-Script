package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
@Slf4j
public class LoginMode extends ModeStrategy {

    private static Timer timer;

    public static void cancelTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.LOGIN);
        log.info("切換到" + ModeEnum.LOGIN.getComment());
    }

    @Override
    protected void nextStep() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SystemUtil.frontWindow(Core.getGameHWND());
                WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
                MouseUtil.mouseLeftButtonClick(rect.left + 100, (rect.top + rect.bottom) >> 1);
            }
        }, 3000, 2000);
    }
}
