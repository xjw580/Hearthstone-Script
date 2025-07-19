package club.xiaojiawei.hsscript.consts

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.ScriptStatus.maxLogSizeB
import club.xiaojiawei.hsscript.status.ScriptStatus.maxLogSizeKB
import club.xiaojiawei.hsscript.utils.ConfigUtil
import java.awt.Toolkit
import java.time.ZoneOffset

/**
 * 存储脚本常量
 * @author 肖嘉威
 * @date 2023/7/3 21:12
 */

val SCREEN_SCALE = Toolkit.getDefaultToolkit().screenResolution / 96.0

const val GAME_CN_NAME: String = "炉石传说"
const val PLATFORM_CN_NAME: String = "战网"
const val PLATFORM_LOGIN_CN_NAME: String = "战网登录"
const val GAME_US_NAME: String = "Hearthstone"
const val PLATFORM_US_NAME: String = "Battle.net"

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
const val GAME_PROGRAM_NAME: String = "$GAME_US_NAME.exe"

/**
 * 战网程序名
 */
const val PLATFORM_PROGRAM_NAME: String = "$PLATFORM_US_NAME.exe"

/**
 * 作者
 */
const val AUTHOR: String = "XiaoJiawei"

val ZONE_OFFSET = ZoneOffset.ofHours(8)


/*日志相关*/
const val VALUE: String = "value"
const val TAG: String = "tag"
const val BLOCK_TYPE: String = "BlockType"
const val BLOCK_START_NULL: String = "Block Start=(null)"
const val BLOCK_END: String = "BLOCK_END"
const val BLOCK_END_NULL: String = "Block End=(null)"
const val SHOW_ENTITY: String = "SHOW_ENTITY"
const val ENTITY: String = "Entity"
const val FULL_ENTITY: String = "FULL_ENTITY"
const val TAG_CHANGE: String = "TAG_CHANGE"
const val CHANGE_ENTITY: String = "CHANGE_ENTITY"
const val LOST: String = "LOST"
const val WON: String = "WON"
const val CONCEDED: String = "CONCEDED"
const val ARG_PAUSE: String = "--pause="
const val ARG_PAGE: String = "--page="