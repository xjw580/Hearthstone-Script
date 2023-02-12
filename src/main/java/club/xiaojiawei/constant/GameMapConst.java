package club.xiaojiawei.constant;

import club.xiaojiawei.entity.area.Area;
import club.xiaojiawei.enums.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 肖嘉威
 * @date 2022/12/11 19:29
 */
public class GameMapConst {

    /**
     * 存放所有卡牌所在的区域
     */
    public static final Map<String, Area> CARD_AREA_MAP = new HashMap<>();
    public static final Map<String, ModeEnum> MODE_MAP;
    public static final Map<String, CardTypeEnum> CARD_TYPE_MAP;
    public static final Map<String, ZoneEnum> ZONE_MAP;
    public static final Map<String, TagEnum> TAG_MAP;
    public static final Map<String, BlockTypeEnum> BLOCK_TYPE_MAP;
    public static final Map<String, DeckEnum> DECK_MAP;
    public static final Map<String, DeckTypeEnum> DECK_TYPE_MAP;

    static {
        MODE_MAP = new HashMap<>(ModeEnum.values().length);
        for (ModeEnum value : ModeEnum.values()) {
            MODE_MAP.put(value.getValue(), value);
        }
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
