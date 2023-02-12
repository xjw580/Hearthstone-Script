package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.constant.GameRatioConst.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
@Slf4j
public class LoginAbstractModeStrategy extends AbstractModeStrategy {

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
//        去除国服登陆时恼人的点击开始
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!Core.getPause()){
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
                    MouseUtil.leftButtonClick(
                            (rect.right + rect.left) >> 1,
                            (int) (rect.bottom - (rect.bottom - rect.top) * CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                    );
                }
            }
        }, 3000, 2000);
    }
}
