package club.xiaojiawei.hsscript.consts

import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.hsscript.enums.TagEnum
import club.xiaojiawei.status.War
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Robot
import java.awt.Toolkit
import java.awt.geom.AffineTransform
import java.io.File
import java.util.Collections

/**
 * 存储脚本常量
 * @author 肖嘉威
 * @date 2023/7/3 21:12
 */
object ScriptStaticData {

    /**
     * 是否设置了炉石和战网的路径
     */
    var setPath = true

    /**
     * 游戏窗口句柄
     */
    private val gameHWND: HWND? = null
    const val GAME_CN_NAME: String = "炉石传说"
    const val PLATFORM_CN_NAME: String = "战网"
    const val PLATFORM_LOGIN_CN_NAME: String = "战网登录"
    const val GAME_US_NAME: String = "Hearthstone"
    const val PLATFORM_US_NAME: String = "Battle.net"
    const val TEMP_VERSION_DIR: String = "new_version_temp"
    const val UPDATE_PROGRAM_NAME: String = "update.exe"
    const val LIB_DIR: String = "lib"
    @JvmField
    val TEMP_VERSION_PATH: String = System.getProperty("user.dir") + File.separator + TEMP_VERSION_DIR + File.separator
    var MAX_LOG_SIZE_B: Int = 10240 * 1024

    /**
     * 游戏窗口信息
     */
    @JvmField
    val GAME_RECT: WinDef.RECT = WinDef.RECT()

    /**
     * 所有鼠标键盘模拟都需要此对象
     */
    val ROBOT: Robot = Robot()

    /**
     * 显示器横向缩放
     */
    val DISPLAY_SCALE_X: Double

    /**
     * 显示器纵向缩放
     */
    val DISPLAY_SCALE_Y: Double

    /**
     * 显示器纵向像素数
     */
    val DISPLAY_PIXEL_HEIGHT: Int

    /**
     * 显示器横向像素数
     */
    val DISPLAY_PIXEL_WIDTH: Int

    /**
     * 窗口标题栏纵向高度
     */
    val WINDOW_TITLE_PIXEL_Y: Int

    /**
     * 本脚本的程序名
     */
    const val SCRIPT_NAME: String = "hs-script"

    /**
     * 项目名
     */
    const val PROJECT_NAME: String = "Hearthstone-Script"

    /**
     * 炉石传说程序名
     */
    val GAME_PROGRAM_NAME: String = GAME_US_NAME + ".exe"

    /**
     * 战网程序名
     */
    val PLATFORM_PROGRAM_NAME: String = PLATFORM_US_NAME + ".exe"

    /**
     * 作者
     */
    const val AUTHOR: String = "XiaoJiawei"

    /*主路径*/
    const val FXML_PATH: String = "/fxml/"

    /**
     * 图片路径
     */
    val FXML_IMAGE_PATH: String = FXML_PATH + "img/"

    /**
     * 脚本程序图标名字
     */
    const val MAIN_IMG_PNG_NAME: String = "favicon.png"

    /**
     * 脚本程序图标路径
     */
    val SCRIPT_ICON_PATH: String = FXML_IMAGE_PATH + MAIN_IMG_PNG_NAME

    /*日志相关*/
    const val VALUE: String = "value"
    const val TAG: String = "tag"
    const val SHOW_ENTITY: String = "SHOW_ENTITY"
    const val FULL_ENTITY: String = "FULL_ENTITY"
    const val TAG_CHANGE: String = "TAG_CHANGE"
    const val CHANGE_ENTITY: String = "CHANGE_ENTITY"
    const val LOST: String = "LOST"
    const val WON: String = "WON"
    const val CONCEDED: String = "CONCEDED"
    const val COIN: String = "COIN"

    /*游戏数据相关*/ //为什么用Map取枚举而不用valueOf()?因为用valueOf()传入的数据不在枚举中时会直接报错，影响后续运行，而map返回null不影响后续操作
    //    啥时候保证所有数据都在枚举中时就可以删掉map了
    /**
     * 存放所有卡牌所在哪一区域
     */
    val CARD_AREA_MAP: MutableMap<String?, Area?> = HashMap<String?, Area?>()
    val TAG_MAP: MutableMap<String?, TagEnum?>
    val CARD_RACE_MAP: MutableMap<String?, CardRaceEnum?>
    val CARD_TYPE_MAP: MutableMap<String?, CardTypeEnum?>

    init {
        War.addResetCallback { CARD_AREA_MAP.clear() }
        val screenDevices: Array<GraphicsDevice?> = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()
        //        if (screenDevices.length > 1){
//            log.info("检测到有多台显示器，请将炉石传说放到主显示器运行");
//        }
        val tx: AffineTransform? = screenDevices[0]?.defaultConfiguration?.defaultTransform
        DISPLAY_SCALE_X = tx?.scaleX ?:1.0
        DISPLAY_SCALE_Y = tx?.scaleY ?:1.0
        DISPLAY_PIXEL_WIDTH = (Toolkit.getDefaultToolkit().getScreenSize().width * DISPLAY_SCALE_X).toInt()
        DISPLAY_PIXEL_HEIGHT = (Toolkit.getDefaultToolkit().getScreenSize().height * DISPLAY_SCALE_Y).toInt()
        WINDOW_TITLE_PIXEL_Y = (31 / DISPLAY_SCALE_Y).toInt()

        val tagTempMap: MutableMap<String?, TagEnum?> = HashMap(TagEnum.entries.size)
        for (value in TagEnum.entries) {
            tagTempMap.put(value.name, value)
        }
        TAG_MAP = Collections.unmodifiableMap<String?, TagEnum?>(tagTempMap)

        val cardRaceTempMap: MutableMap<String?, CardRaceEnum?> =
            HashMap(CardRaceEnum.entries.size)
        for (value in CardRaceEnum.entries) {
            cardRaceTempMap.put(value.name, value)
        }
        CARD_RACE_MAP = Collections.unmodifiableMap<String?, CardRaceEnum?>(cardRaceTempMap)

        val cardTypeTempMap: MutableMap<String?, CardTypeEnum?> =
            HashMap(CardTypeEnum.entries.size)
        for (value in CardTypeEnum.entries) {
            cardTypeTempMap.put(value.name, value)
        }
        CARD_TYPE_MAP = Collections.unmodifiableMap<String?, CardTypeEnum?>(cardTypeTempMap)
    }
}
