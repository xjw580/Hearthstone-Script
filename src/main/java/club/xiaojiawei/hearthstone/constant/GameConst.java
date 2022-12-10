package club.xiaojiawei.hearthstone.constant;

import club.xiaojiawei.hearthstone.entity.area.Area;
import club.xiaojiawei.hearthstone.enums.*;

import java.util.HashMap;
import java.util.Map;

import static club.xiaojiawei.hearthstone.enums.BlockTypeEnum.*;
import static club.xiaojiawei.hearthstone.enums.CardTypeEnum.*;
import static club.xiaojiawei.hearthstone.enums.DeckTypeEnum.*;
import static club.xiaojiawei.hearthstone.enums.ModeEnum.*;
import static club.xiaojiawei.hearthstone.enums.TagEnum.*;
import static club.xiaojiawei.hearthstone.enums.ZoneEnum.PLAY;
import static club.xiaojiawei.hearthstone.enums.ZoneEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/11/27 1:06
 */
public class GameConst {

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
    public static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.255;
    public static final Map<String, ModeEnum> MODE_MAP;
    public static final Map<String, CardTypeEnum> CARD_TYPE_MAP;
    public static final Map<String, ZoneEnum> ZONE_MAP;
    public static final Map<String, TagEnum> TAG_MAP;
    public static final Map<String, BlockTypeEnum> BLOCK_TYPE_MAP;
    public static final Map<String, Area> CARD_AREA_MAP = new HashMap<>();
    public static final Map<String, DeckEnum> DECK_MAP;
    public static final Map<String, DeckTypeEnum> DECK_TYPE_MAP;
    public static final String VALUE = "value";
    public static final String SHOW_ENTITY = "SHOW_ENTITY";
    public static final String FULL_ENTITY = "FULL_ENTITY";
    public static final String TAG_CHANGE = "TAG_CHANGE";
    static {
        MODE_MAP = Map.ofEntries(
                Map.entry("LOGIN", LOGIN),
                Map.entry("HUB", HUB),
                Map.entry("TOURNAMENT", TOURNAMENT),
                Map.entry("PACKOPENING", PACKOPENING),
                Map.entry("COLLECTIONMANAGER", COLLECTIONMANAGER),
                Map.entry("ADVENTURE", ADVENTURE),
                Map.entry("LETTUCE_MAP", LETTUCE_MAP),
                Map.entry("BACON", BACON),
                Map.entry("GAMEPLAY", GAMEPLAY),
                Map.entry("GAME_MODE", GAME_MODE)
        );
        CARD_TYPE_MAP = Map.ofEntries(
                Map.entry("MINION", MINION),
                Map.entry("SPELL", SPELL),
                Map.entry("HERO", HERO),
                Map.entry("WEAPON", WEAPON),
                Map.entry("HERO_POWER", HERO_POWER)
        );
        ZONE_MAP = Map.ofEntries(
                Map.entry("DECK", DECK),
                Map.entry("HAND", HAND),
                Map.entry("PLAY", PLAY),
                Map.entry("SETASIDE", SETASIDE),
                Map.entry("SECRET", SECRET),
                Map.entry("GRAVEYARD", GRAVEYARD),
                Map.entry("REMOVEDFROMGAME", REMOVEDFROMGAME)
        );
        TAG_MAP = Map.ofEntries(
                Map.entry("MULLIGAN_STATE", MULLIGAN_STATE),
                Map.entry("RESOURCES", RESOURCES),
                Map.entry("RESOURCES_USED", RESOURCES_USED),
                Map.entry("TEMP_RESOURCES", TEMP_RESOURCES),
                Map.entry("STEP", STEP),
                Map.entry("NEXT_STEP", NEXT_STEP),
                Map.entry("CURRENT_PLAYER", CURRENT_PLAYER),
                Map.entry("ZONE_POSITION", ZONE_POSITION),
                Map.entry("ZONE", ZONE),
                Map.entry("HEALTH", HEALTH),
                Map.entry("ATK", ATK),
                Map.entry("COST", COST),
                Map.entry("FROZEN", FROZEN),
                Map.entry("EXHAUSTED", EXHAUSTED),
                Map.entry("PLAYSTATE", PLAYSTATE),
                Map.entry("FIRST_PLAYER", FIRST_PLAYER),
                Map.entry("DAMAGE", DAMAGE),
                Map.entry("TAUNT", TAUNT),
                Map.entry("DIVINE_SHIELD", DIVINE_SHIELD),
                Map.entry("AURA", AURA),
                Map.entry("ARMOR", ARMOR),
                Map.entry("STEALTH", STEALTH)
        );
        BLOCK_TYPE_MAP = Map.ofEntries(
                Map.entry("TRIGGER", TRIGGER),
                Map.entry("PLAY", BlockTypeEnum.PLAY),
                Map.entry("POWER", POWER),
                Map.entry("ATTACK", ATTACK),
                Map.entry("DEATHS", DEATHS),
                Map.entry("FATIGUE", FATIGUE)
        );
        DECK_MAP = Map.ofEntries(
                Map.entry("ZOO", DeckEnum.ZOO),
                Map.entry("FREE", DeckEnum.FREE)
        );
        DECK_TYPE_MAP = Map.ofEntries(
                Map.entry("CLASSIC", CLASSIC),
                Map.entry("WILD", WILD),
                Map.entry("STANDARD", STANDARD),
                Map.entry("ROOKIE", ROOKIE)
        );
    }
}
