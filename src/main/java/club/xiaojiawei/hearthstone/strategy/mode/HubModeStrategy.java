package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.hearthstone.constant.GameConst.CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:36
 */
@Slf4j
public class HubModeStrategy extends ModeStrategy {

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.HUB);
        log.info("切換到" + ModeEnum.HUB.getComment());
    }

    @Override
    protected void nextStep() {
//        去除各种推广（任务，活动等）
        log.info("去除各种推广（任务，活动等）");
        for (int i = 0; i < 4; i++) {
            SystemUtil.frontWindow(Core.getGameHWND());
            WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
//            todo 位置不对，要点取消按钮
            MouseUtil.leftButtonClick(
                    (rect.right + rect.left) >> 1,
                    (int) (rect.bottom - (rect.bottom - rect.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION)
            );
            ROBOT.delay(500);
        }
        Core.enterMode();
    }
}
