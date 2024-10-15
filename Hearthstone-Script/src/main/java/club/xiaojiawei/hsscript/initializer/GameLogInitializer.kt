package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.FileUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.util.isTrue
import org.ini4j.Ini
import java.nio.file.Path

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
object GameLogInitializer : AbstractInitializer() {

    private fun checkLogConfig(ini: Ini, section: String): Boolean {
        var modify = false

        var key = "LogLevel"
        var logLevel = ini.get(section, key)
        if (logLevel == null || logLevel.toIntOrNull() == null || logLevel.toInt() != 1) {
            ini.put(section, key, 1)
            modify = true
        }

        key = "FilePrinting"
        logLevel = ini.get(section, key)
        if (logLevel == null || !logLevel.toBoolean()) {
            ini.put(section, key, "True")
            modify = true
        }

        key = "Verbose"
        logLevel = ini.get(section, key)
        if (logLevel == null || !logLevel.toBoolean()) {
            ini.put(section, key, "True")
            modify = true
        }
        return modify
    }

    public override fun exec() {
        val gameLogConfigFile = Path.of(System.getenv("LOCALAPPDATA"), "Blizzard", "Hearthstone", "log.config").toFile()
        if (gameLogConfigFile.isDirectory || !gameLogConfigFile.exists()) {
            FileUtil.deleteFile(gameLogConfigFile)
            gameLogConfigFile.parentFile.mkdirs()
            gameLogConfigFile.createNewFile()
        }
        val gameLogIni = Ini(gameLogConfigFile)

        var modify = false

        checkLogConfig(gameLogIni, "LoadingScreen").isTrue { modify = true }
        checkLogConfig(gameLogIni, "Decks").isTrue { modify = true }
        checkLogConfig(gameLogIni, "Power").isTrue { modify = true }

        modify.isTrue {
            gameLogIni.store()
            SystemUtil.notice("炉石日志配置已更改，请重启游戏，否则脚本将无法运行")
            log.info { "炉石日志配置已更改，请重启游戏，否则脚本将无法运行" }
        }

        modify = false

        val gamePath = ConfigUtil.getString(ConfigEnum.GAME_PATH)
        if (gamePath.isBlank()) {
            log.warn { "未设置游戏安装目录，无法修改日志大小限制" }
            return
        }
        val clientConfigFile = Path.of(gamePath, "client.config").toFile()
        if (clientConfigFile.isDirectory || !clientConfigFile.exists()) {
            FileUtil.deleteFile(clientConfigFile)
            clientConfigFile.parentFile.mkdirs()
            clientConfigFile.createNewFile()
        }
        val clientIni = Ini(clientConfigFile)

        val logLimitSize = clientIni.get("Log", "FileSizeLimit.Int")
        val shouldLogLimitSize = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)
        if (logLimitSize == null || logLimitSize.toIntOrNull() == null
            || logLimitSize.toInt() != shouldLogLimitSize
        ) {
            modify = true
            clientIni.put("Log", "FileSizeLimit.Int", shouldLogLimitSize)
        }

        modify.isTrue {
            clientIni.store()
            log.info { "游戏日志大小限制已修改为${shouldLogLimitSize}KB，重启游戏生效" }
        }

    }

}
