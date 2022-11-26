package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
public class ReadyTurnPhaseStrategy extends PhaseStrategy {

    private static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.20;

    @Override
    public void afterInto() {
        WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());

    }
}
