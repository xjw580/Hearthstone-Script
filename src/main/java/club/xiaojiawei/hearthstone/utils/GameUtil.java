package club.xiaojiawei.hearthstone.utils;

import club.xiaojiawei.hearthstone.run.Core;
import com.sun.jna.platform.win32.WinDef;

import static club.xiaojiawei.hearthstone.constant.GameRatioConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
public class GameUtil {

    public static void clickBackButton(WinDef.RECT gameRECT){
        MouseUtil.leftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) + ((gameRECT.bottom - gameRECT.top) * BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO) + RandomUtil.getRandom(-5, 5)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-2, 2)
        );
    }

    public static void surrender(){
        ROBOT.keyPress(27);
        ROBOT.keyRelease(27);
        ROBOT.delay(2000);
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                rect.right + rect.left >> 1,
                (int) (rect.bottom - (rect.bottom - rect.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
    }

    public static final float SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.652;
}
