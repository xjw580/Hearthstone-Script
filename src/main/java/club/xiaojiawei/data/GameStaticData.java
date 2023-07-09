package club.xiaojiawei.data;

import club.xiaojiawei.entity.area.Area;
import club.xiaojiawei.enums.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 肖嘉威
 * @date 2023/7/3 21:08
 * @msg 游戏变量
 */
public class GameStaticData {
/*游戏日志相关*/
    public static final String VALUE = "value";
    public static final String SHOW_ENTITY = "SHOW_ENTITY";
    public static final String FULL_ENTITY = "FULL_ENTITY";
    public static final String TAG_CHANGE = "TAG_CHANGE";
    public static final String LOST = "LOST";
    public static final String WON = "WON";
    public static final String CONCEDED = "CONCEDED";
    public static final String COIN = "COIN";
/*游戏UI相关*/
    public static final float GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO = (float) 1.351;
    public static final float FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO = (float) 0.75;
    public static final float START_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) 0.313;
    public static final float START_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float)  0.186;
    public static final float SELECT_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.305;
    public static final float BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.437;
    public static final float BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.085;
    public static final float FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD = (float) 0.24;
    public static final float CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD = (float) 0.233;
    public static final float FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD = (float) 0.267;
    public static final float CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD = (float) 0.173;
    public static final float CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.255;

/*游戏数据相关*/
//为什么用Map取枚举而不用valueOf()?因为用valueOf()传入的数据不在枚举中时会直接报错，影响后续运行，而map返回null不影响其他操作
    /**
     * 存放所有卡牌所在哪一区域
     */
    public static final Map<String, Area> CARD_AREA_MAP = new HashMap<>();
    public static final Map<String, CardTypeEnum> CARD_TYPE_MAP;
    public static final Map<String, ZoneEnum> ZONE_MAP;
    public static final Map<String, TagEnum> TAG_MAP;
    public static final Map<String, BlockTypeEnum> BLOCK_TYPE_MAP;
    public static final Map<String, DeckEnum> DECK_MAP;
    public static final Map<String, DeckTypeEnum> DECK_TYPE_MAP;

    static {
        CARD_TYPE_MAP = new HashMap<>(CardTypeEnum.values().length);
        for (CardTypeEnum value : CardTypeEnum.values()) {
            CARD_TYPE_MAP.put(value.getValue(), value);
        }
        ZONE_MAP = new HashMap<>(ZoneEnum.values().length);
        for (ZoneEnum value : ZoneEnum.values()) {
            ZONE_MAP.put(value.getValue(), value);
        }
        TAG_MAP = new HashMap<>(TagEnum.values().length);
        for (TagEnum value : TagEnum.values()) {
            TAG_MAP.put(value.getValue(), value);
        }
        BLOCK_TYPE_MAP = new HashMap<>(BlockTypeEnum.values().length);
        for (BlockTypeEnum value : BlockTypeEnum.values()) {
            BLOCK_TYPE_MAP.put(value.getValue(), value);
        }
        DECK_MAP = new HashMap<>(DeckEnum.values().length);
        for (DeckEnum value : DeckEnum.values()) {
            DECK_MAP.put(value.getValue(), value);
        }
        DECK_TYPE_MAP = new HashMap<>(DeckTypeEnum.values().length);
        for (DeckTypeEnum value : DeckTypeEnum.values()) {
            DECK_TYPE_MAP.put(value.getValue(), value);
        }
    }

}
