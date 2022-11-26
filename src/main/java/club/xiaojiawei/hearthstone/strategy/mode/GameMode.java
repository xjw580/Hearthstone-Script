package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hearthstone.constant.GameConst.MODE_MAP;
import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2022/11/26 21:44
 */
@Slf4j
public class GameMode extends ModeStrategy {

    public static final float GAME_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.475;

    @Override
    public void intoMode() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Mode.getCurrMode() != ModeEnum.GAME_MODE){
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                    MouseUtil.mouseLeftButtonClick(
                            ((gameRECT.right + gameRECT.left) >> 1) + RandomUtil.getRandom(-15, 15),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * GAME_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
                    );
                }else {
                    timer.cancel();
                }
            }
        }, delayTime, intervalTime);
    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.GAME_MODE);
        log.info("切換到" + ModeEnum.GAME_MODE.getComment());
        MODE_MAP.getOrDefault(PROPERTIES.getProperty("mode"), ModeEnum.UNKNOWN).getModeStrategy().get().intoMode();
    }

    @Override
    protected void nextStep() {

    }
}
