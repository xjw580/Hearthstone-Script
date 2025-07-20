package club.xiaojiawei.hsscript.consts

import club.xiaojiawei.hsscript.utils.SystemUtil
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * @author 肖嘉威
 * @date 2024/10/13 16:45
 */

val ROOT_PATH = System.getProperty("user.dir")

val TEMP_VERSION_PATH: String = Path.of(ROOT_PATH, "new_version_temp").toString()

val LOG_PATH: String = Path.of(ROOT_PATH, "log").toString()

val LIBRARY_PATH: String = Path.of(ROOT_PATH, "lib").toString()
val DLL_PATH: String = Path.of(LIBRARY_PATH, "dll").toString()

val CONFIG_PATH: String = Path.of(ROOT_PATH, "config").toString()

val PLUGIN_PATH: String = Path.of(ROOT_PATH, "plugin").toString()

val CARD_WEIGHT_CONFIG_PATH: Path = Path.of(CONFIG_PATH, "card.weight");

val CARD_INFO_CONFIG_PATH: Path = Path.of(CONFIG_PATH, "card.info");

const val FXML_DIR: String = "/fxml/"

const val GAME_LOG_DIR: String = "Logs"

const val DRIVE_PATH = "C:\\Windows\\System32\\drivers"

val MOUSE_DRIVE_PATH: String = Path.of(DRIVE_PATH, "mouse.sys").toString()

val KEYBOARD_DRIVE_PATH: String = Path.of(DRIVE_PATH, "keyboard.sys").toString()

val DATA_DIR: Path = Path.of(ROOT_PATH, "data")

const val STATISTICS_DB_NAME: String = "statistics.db"

@JvmInline
value class ResourceFile(val name: String)

val INJECT_UTIL_FILE = ResourceFile("inject-util.exe")

val INSTALL_DRIVE_FILE = ResourceFile("install-drive.exe")

val HS_CARD_UTIL_FILE = ResourceFile("card-update-util.exe")

val UPDATE_FILE = ResourceFile("update.exe")

val LIB_HS_FILE = ResourceFile("hs.dll")

val LIB_BN_FILE = ResourceFile("bn.dll")

const val GAME_WAR_LOG_NAME = "Power.log"

const val GAME_MODE_LOG_NAME = "LoadingScreen.log"

const val COMMON_CSS_PATH = "/fxml/css/common.css"

/**
 * 图片路径
 */
val IMG_PATH by lazy {
    val imgDir = "resources/img"
    val jarDir = File(
        SystemUtil.javaClass.getProtectionDomain()
            .codeSource
            .location
            .toURI()
    )
    var imgPath = Path.of(
        jarDir.path, imgDir
    )
    if (!imgPath.exists()) {
        imgPath = Path.of(jarDir.parentFile.path, imgDir)
    }
    imgPath.toString()
}

/**
 * 脚本程序图标名字
 */
const val MAIN_IMG_NAME: String = "favicon.png"

const val TRAY_IMG_NAME: String = "favicon.ico"

const val TRAY_SETTINGS_IMG_NAME: String = "settings.ico"

const val TRAY_STATISTICS_IMG_NAME: String = "statistics.ico"
const val TRAY_START_IMG_NAME: String = "start.ico"
const val TRAY_PAUSE_IMG_NAME: String = "pause.ico"
const val TRAY_EXIT_IMG_NAME: String = "exit.ico"