package club.xiaojiawei.hsscript.consts

import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Robot

/**
 * 存储脚本常量
 * @author 肖嘉威
 * @date 2023/7/3 21:12
 */
/**
 * 是否设置了炉石和战网的路径
 */
var setPath = true

/**
 * 游戏窗口句柄
 */
var GAME_HWND: HWND? = null
const val GAME_CN_NAME: String = "炉石传说"
const val PLATFORM_CN_NAME: String = "战网"
const val PLATFORM_LOGIN_CN_NAME: String = "战网登录"
const val GAME_US_NAME: String = "Hearthstone"
const val PLATFORM_US_NAME: String = "Battle.net"

var MAX_LOG_SIZE_KB: Int = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)

var MAX_LOG_SIZE_B: Int = MAX_LOG_SIZE_KB * 1024

/**
 * 游戏窗口信息
 */
val GAME_RECT: WinDef.RECT = WinDef.RECT()

/**
 * 所有鼠标键盘模拟都需要此对象
 */
val ROBOT: Robot = Robot()

/**
 * 显示器横向缩放
 */
var DISPLAY_SCALE_X: Double = 1.0

/**
 * 显示器纵向缩放
 */
var DISPLAY_SCALE_Y: Double = 1.0

/**
 * 显示器纵向像素数
 */
var DISPLAY_PIXEL_HEIGHT: Int = 0

/**
 * 显示器横向像素数
 */
var DISPLAY_PIXEL_WIDTH: Int = 0

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
const val MAIN_IMG_NAME: String = "favicon.png"

/**
 * 脚本程序图标路径
 */
val SCRIPT_ICON_PATH: String = FXML_IMAGE_PATH + MAIN_IMG_NAME

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

//init{
//    WarEx.addResetCallback { CARD_AREA_MAP.clear() }
//    val screenDevices: Array<GraphicsDevice?> = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()
//    //        if (screenDevices.length > 1){
////            log.info("检测到有多台显示器，请将炉石传说放到主显示器运行");
////        }
//    val tx: AffineTransform? = screenDevices[0]?.defaultConfiguration?.defaultTransform
//    DISPLAY_SCALE_X = tx?.scaleX ?: 1.0
//    DISPLAY_SCALE_Y = tx?.scaleY ?: 1.0
//    DISPLAY_PIXEL_WIDTH = (Toolkit.getDefaultToolkit().getScreenSize().width * DISPLAY_SCALE_X).toInt()
//    DISPLAY_PIXEL_HEIGHT = (Toolkit.getDefaultToolkit().getScreenSize().height * DISPLAY_SCALE_Y).toInt()
//}

fun reload() {
    MAX_LOG_SIZE_KB = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)
    MAX_LOG_SIZE_B = MAX_LOG_SIZE_KB * 1024
}