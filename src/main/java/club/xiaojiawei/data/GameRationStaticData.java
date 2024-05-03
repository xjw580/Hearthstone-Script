package club.xiaojiawei.data;

/**
 * 游戏常量
 * @author 肖嘉威
 * @date 2023/7/3 21:08
 */
public class GameRationStaticData {
    /*我的屏幕高30.5*/
    public static final float GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO = 1.351F;
    public static final float FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO = 0.75F;
    public static final float START_BUTTON_HORIZONTAL_TO_CENTER_RATIO = 0.313F;
    public static final float START_BUTTON_VERTICAL_TO_BOTTOM_RATIO =  0.186F;
    public static final float SELECT_BUTTON_VERTICAL_TO_BOTTOM_RATIO = 0.305F;
    /**
     * 右下角的返回按钮位置
     */
    public static final float BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.437F;
    public static final float BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.085F;

    public static final float FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD = 0.24F;
    public static final float CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD = 0.233F;
    public static final float FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD = 0.267F;
    public static final float CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD = 0.173F;
    public static final float CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.255F;
    public static final float CANCEL_MATCH_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.148F;
    public static final float[] FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION = new float[]{
            -0.033F, -0.08F, -0.123F, -0.167F, -0.177F, -0.193F, -0.203F, -0.213F, -0.22F, -0.227F
    };
    public static final float[] HAND_CARD_HORIZONTAL_CLEARANCE_RATION = new float[]{
            0F, 0.09F, 0.09F, 0.087F, 0.07F, 0.057F, 0.05F, 0.042F, 0.037F, 0.034F
    };
    public static final float HAND_CARD_VERTICAL_TO_BOTTOM_RATION = 0.059F;
    public static final float RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION = 0.801F;
    public static final float MY_HERO_VERTICAL_TO_BOTTOM_RATION = 0.26F;
    public static final float PLAY_CARD_HORIZONTAL_CLEARANCE_RATION = 0.097F;
    public static final float MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = 0.45F;
    public static final float RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = 0.62F;
    public static final float POWER_VERTICAL_TO_BOTTOM_RATION = 0.23F;
    public static final float POWER_HORIZONTAL_TO_CENTER_RATION = 0.133F;
    public static final float TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.54F;
    public static final float TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.417F;
    public static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.23F;
}
