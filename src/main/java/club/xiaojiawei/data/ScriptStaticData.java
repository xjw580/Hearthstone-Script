package club.xiaojiawei.data;

import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import club.xiaojiawei.enums.TagEnum;
import com.sun.jna.platform.win32.WinDef;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储脚本常量
 * @author 肖嘉威
 * @date 2023/7/3 21:12
 */
@Slf4j
public class ScriptStaticData {

    /**
     *  是否设置了炉石和战网的路径
     */
    @Getter
    @Setter
    private static boolean isSetPath = true;
    /**
     * 游戏窗口句柄
     */
    @Getter
    @Setter
    private static WinDef.HWND gameHWND;
    public static final String GAME_CN_NAME = "炉石传说";
    public static final String PLATFORM_CN_NAME = "战网";
    public static final String PLATFORM_LOGIN_CN_NAME = "战网登录";
    public static final String GAME_US_NAME = "Hearthstone";
    public static final String PLATFORM_US_NAME = "Battle.net";
    public static final String TEMP_VERSION_DIR = "new_version_temp";
    public static final String UPDATE_PROGRAM_NAME = "update.exe";
    public static final String TEMP_VERSION_PATH = System.getProperty("user.dir") + File.separator + TEMP_VERSION_DIR + File.separator;
    /**
     * 游戏窗口信息
     */
    public static final WinDef.RECT GAME_RECT = new WinDef.RECT();
    /**
     * 所有鼠标键盘模拟都需要此对象
     */
    public static final Robot ROBOT;
    /**
     * 显示器横向缩放
     */
    public static final double DISPLAY_SCALE_X;
    /**
     * 显示器纵向缩放
     */
    public static final double DISPLAY_SCALE_Y;
    /**
     * 显示器纵向像素数
     */
    public static final int DISPLAY_PIXEL_HEIGHT;
    /**
     * 显示器横向像素数
     */
    public static final int DISPLAY_PIXEL_WIDTH;
    /**
     * 窗口标题栏纵向高度
     */
    public static final int WINDOW_TITLE_PIXEL_Y;
    /**
     * 本脚本的程序名
     */
    public static final String SCRIPT_NAME = "hs-script";
    /**
     * 项目名
     */
    public static final String PROJECT_NAME = "Hearthstone-Script";
    /**
     * 炉石传说程序名
     */
    public static final String GAME_PROGRAM_NAME = GAME_US_NAME + ".exe";
    /**
     * 战网程序名
     */
    public static final String PLATFORM_PROGRAM_NAME = PLATFORM_US_NAME + ".exe";
    /**
     * 作者
     */
    public static final String AUTHOR = "XiaoJiawei";
    /*主路径*/
    public static final String FXML_PATH = "/fxml/";
    /**
     * 图片路径
     */
    public static final String FXML_IMAGE_PATH = FXML_PATH + "img/";
    /**
     * 脚本程序图标名字
     */
    public static final String MAIN_IMG_PNG_NAME = "favicon.png";
    /**
     * 脚本程序图标路径
     */
    public static final String SCRIPT_ICON_PATH = FXML_IMAGE_PATH + MAIN_IMG_PNG_NAME;

    /*日志相关*/
    public static final String VALUE = "value";
    public static final String TAG = "tag";
    public static final String SHOW_ENTITY = "SHOW_ENTITY";
    public static final String FULL_ENTITY = "FULL_ENTITY";
    public static final String TAG_CHANGE = "TAG_CHANGE";
    public static final String CHANGE_ENTITY = "CHANGE_ENTITY";
    public static final String LOST = "LOST";
    public static final String WON = "WON";
    public static final String CONCEDED = "CONCEDED";
    public static final String COIN = "COIN";
    public static final String UNKNOWN = "UNKNOWN ENTITY";
    public static final String LOG_FIELD_NAME = "log";

    /*游戏数据相关*/
//为什么用Map取枚举而不用valueOf()?因为用valueOf()传入的数据不在枚举中时会直接报错，影响后续运行，而map返回null不影响后续操作
//    啥时候保证所有数据都在枚举中时就可以删掉map了
    /**
     * 存放所有卡牌所在哪一区域
     */
    public static final Map<String, Area> CARD_AREA_MAP = new HashMap<>();
    public static final Map<String, TagEnum> TAG_MAP;
    public static final Map<String, CardRaceEnum> CARD_RACE_MAP;
    public static final Map<String, CardTypeEnum> CARD_TYPE_MAP;

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screenDevices.length > 1){
            log.info("检测到有多台显示器，请将炉石传说放到主显示器运行");
        }
        AffineTransform tx = screenDevices[0].getDefaultConfiguration().getDefaultTransform();
        DISPLAY_SCALE_X = tx.getScaleX();
        DISPLAY_SCALE_Y = tx.getScaleY();
        DISPLAY_PIXEL_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * DISPLAY_SCALE_X);
        DISPLAY_PIXEL_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * DISPLAY_SCALE_Y);
        WINDOW_TITLE_PIXEL_Y = (int) (33 / DISPLAY_SCALE_Y);

        Map<String, TagEnum> tagTempMap = new HashMap<>(TagEnum.values().length);
        for (TagEnum value : TagEnum.values()) {
            tagTempMap.put(value.name(), value);
        }
        TAG_MAP = Collections.unmodifiableMap(tagTempMap);

        Map<String, CardRaceEnum> cardRaceTempMap =  new HashMap<>(CardRaceEnum.values().length);
        for (CardRaceEnum value : CardRaceEnum.values()) {
            cardRaceTempMap.put(value.name(), value);
        }
        CARD_RACE_MAP = Collections.unmodifiableMap(cardRaceTempMap);

        Map<String, CardTypeEnum> cardTypeTempMap = new HashMap<>(CardTypeEnum.values().length);
        for (CardTypeEnum value : CardTypeEnum.values()) {
            cardTypeTempMap.put(value.name(), value);
        }
        CARD_TYPE_MAP = Collections.unmodifiableMap(cardTypeTempMap);
    }

}
