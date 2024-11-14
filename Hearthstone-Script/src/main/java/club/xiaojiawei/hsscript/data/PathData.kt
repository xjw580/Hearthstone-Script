package club.xiaojiawei.hsscript.data

import java.nio.file.Path

/**
 * @author 肖嘉威
 * @date 2024/10/13 16:45
 */

private val ROOT_PATH = System.getProperty("user.dir")

val MAIN_PATH = ROOT_PATH

val TEMP_VERSION_PATH: String = Path.of(ROOT_PATH, "new_version_temp").toString()

val LIBRARY_PATH: String = Path.of(ROOT_PATH, "lib").toString()
val DLL_PATH: String = Path.of(LIBRARY_PATH, "dll").toString()

val RESOURCE_PATH: String = Path.of(ROOT_PATH, "resource").toString()

val CONFIG_PATH: String = Path.of(ROOT_PATH, "config").toString()

val PLUGIN_PATH: String = Path.of(ROOT_PATH, "plugin").toString()

val WEIGHT_CONFIG_PATH = Path.of(CONFIG_PATH, "card.weight");

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