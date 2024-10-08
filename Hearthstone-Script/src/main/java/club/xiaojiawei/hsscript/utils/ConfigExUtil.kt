package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.enums.ConfigEnum
import java.io.File

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/10/2 13:19
 */
object ConfigExUtil {

    fun storeGamePath(gameInstallPath: String?): Boolean {
        gameInstallPath?:return false
        if (File(gameInstallPath + File.separator + ScriptStaticData.GAME_PROGRAM_NAME).exists()) {
            ConfigUtil.putString(ConfigEnum.GAME_PATH, gameInstallPath)
            return true
        }
        return false
    }

    fun storePlatformPath(platformInstallPath: String?): Boolean {
        platformInstallPath?:return false
        val programAbsolutePath = if (platformInstallPath.endsWith(".exe")) {
            platformInstallPath
        } else {
            platformInstallPath + File.separator + ScriptStaticData.PLATFORM_PROGRAM_NAME
        }
        if (File(programAbsolutePath).exists()) {
            ConfigUtil.putString(ConfigEnum.PLATFORM_PATH, programAbsolutePath)
            return true
        }
        return false
    }

}