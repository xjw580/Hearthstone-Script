package club.xiaojiawei.hsscript.data

import club.xiaojiawei.hsscript.utils.SystemUtil
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * @author 肖嘉威
 * @date 2024/10/13 16:45
 */

val ROOT_PATH = System.getProperty("user.dir")

val MAIN_PATH = ROOT_PATH

val TEMP_VERSION_PATH: String = Path.of(ROOT_PATH, "new_version_temp").toString()

val LOG_PATH: String = Path.of(ROOT_PATH, "log").toString()

val LIBRARY_PATH: String = Path.of(ROOT_PATH, "lib").toString()
val DLL_PATH: String = Path.of(LIBRARY_PATH, "dll").toString()

val CONFIG_PATH: String = Path.of(ROOT_PATH, "config").toString()

val PLUGIN_PATH: String = Path.of(ROOT_PATH, "plugin").toString()

val WEIGHT_CONFIG_PATH: Path = Path.of(CONFIG_PATH, "card.weight");

const val FXML_DIR: String = "/fxml/"

const val GAME_LOG_DIR: String = "Logs"

const val DRIVE_PATH = "C:\\Windows\\System32\\drivers"

val MOUSE_DRIVE_PATH: String = Path.of(DRIVE_PATH, "mouse.sys").toString()

val KEYBOARD_DRIVE_PATH: String = Path.of(DRIVE_PATH, "keyboard.sys").toString()

val STATISTICS_DIR: Path = Path.of(ROOT_PATH, "statistics")

const val STATISTICS_DB_NAME: String = "statistics_%s.db"

@JvmInline
value class ResourceFile(val name: String)

val INJECT_UTIL_FILE = ResourceFile("inject-util.exe")

val INSTALL_DRIVE_FILE = ResourceFile("install-drive.exe")

val LIB_HS_FILE = ResourceFile("hs.dll")

val HS_CARD_UTIL_FILE = ResourceFile("card-update-util.exe")

/**
 * 图片路径
 */
val IMG_PATH by lazy {
    val IMG_DIR = "resources/img"
    val jarDir = File(
        SystemUtil.javaClass.getProtectionDomain()
            .codeSource
            .location
            .toURI()
    )
    var imgPath = Path.of(
        jarDir.path, IMG_DIR
    )
    if (!imgPath.exists()) {
        imgPath = Path.of(jarDir.parentFile.path, IMG_DIR)
    }
    imgPath.toString()
}

/**
 * 脚本程序图标名字
 */
const val MAIN_IMG_NAME: String = "favicon.png"