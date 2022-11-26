package club.xiaojiawei.hearthstone.constant;

import club.xiaojiawei.hearthstone.enums.ModeEnum;

import java.util.Map;

import static club.xiaojiawei.hearthstone.enums.ModeEnum.*;
import static club.xiaojiawei.hearthstone.enums.ModeEnum.GAME_MODE;

/**
 * @author 肖嘉威
 * @date 2022/11/27 1:06
 */
public class GameConst {

    public static final float GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO = (float) 1.778;
    public static final float FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO = (float) 0.75;
    public static final float START_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) (GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * 0.23);
    public static final float START_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float)  0.186;
    public static final float SELECT_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.305;
    public static final float BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.32;
    public static final float BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.081;
    public static final Map<String, ModeEnum> MODE_MAP;

    static {
        MODE_MAP = Map.of(
                "LOGIN", LOGIN,
                "HUB", HUB,
                "TOURNAMENT", TOURNAMENT,
                "PACKOPENING", PACKOPENING,
                "COLLECTIONMANAGER", COLLECTIONMANAGER,
                "ADVENTURE", ADVENTURE,
                "LETTUCE_MAP", LETTUCE_MAP,
                "BACON", BACON,
                "GAMEPLAY", GAMEPLAY,
                "GAME_MODE", GAME_MODE
        );
    }
}
