package club.xiaojiawei.utils

import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.data.SpringData
import club.xiaojiawei.enums.ConfigEnum
import jakarta.annotation.Resource
import lombok.Getter
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 脚本配置工具类
 * @author 肖嘉威
 * @date 2023/7/9 2:26
 */
@Component
class PropertiesUtil {

    @Resource
    private val springData: SpringData? = null

    @Resource
    @Getter
    var scriptConfiguration: Properties? = null

    fun storeScriptProperties() {
        try {
            FileOutputStream(springData!!.scriptConfigurationFile).use { fileOutputStream ->
                scriptConfiguration!!.store(fileOutputStream, ScriptStaticData.SCRIPT_NAME)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun storeGamePath(gameInstallPath: String): Boolean {
        if (File(gameInstallPath + File.separator + ScriptStaticData.GAME_PROGRAM_NAME).exists()) {
            scriptConfiguration!!.setProperty(ConfigEnum.GAME_PATH.key, gameInstallPath)
            storeScriptProperties()
            return true
        }
        return false
    }

    fun storePlatformPath(platformInstallPath: String?): Boolean {
        val programAbsolutePath = if (platformInstallPath != null && platformInstallPath.endsWith(".exe")) {
            platformInstallPath
        } else {
            platformInstallPath + File.separator + ScriptStaticData.PLATFORM_PROGRAM_NAME
        }
        if (File(programAbsolutePath).exists()) {
            scriptConfiguration!!.setProperty(ConfigEnum.PLATFORM_PATH.key, programAbsolutePath)
            storeScriptProperties()
            return true
        }
        return false
    }
}
