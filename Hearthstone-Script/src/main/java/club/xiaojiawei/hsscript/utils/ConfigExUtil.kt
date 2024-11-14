package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.WorkDay
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.data.GAME_PROGRAM_NAME
import club.xiaojiawei.hsscript.data.PLATFORM_PROGRAM_NAME
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import java.io.File
import java.nio.file.Path

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/10/2 13:19
 */
object ConfigExUtil {

    fun storeGamePath(gameInstallPath: String?): Boolean {
        gameInstallPath ?: return false

        if (Path.of(gameInstallPath, GAME_PROGRAM_NAME).toFile().exists()) {
            ConfigUtil.putString(ConfigEnum.GAME_PATH, gameInstallPath)
            return true
        }
        return false
    }

    fun storePlatformPath(platformInstallPath: String?): Boolean {
        platformInstallPath ?: return false
        val programAbsolutePath = if (platformInstallPath.endsWith(".exe")) {
            platformInstallPath
        } else {
            platformInstallPath + File.separator + PLATFORM_PROGRAM_NAME
        }
        if (File(programAbsolutePath).exists()) {
            ConfigUtil.putString(ConfigEnum.PLATFORM_PATH, programAbsolutePath)
            return true
        }
        return false
    }

    fun getExitHotKey(): HotKey? {
        return ConfigUtil.getObject(ConfigEnum.EXIT_HOT_KEY, HotKey::class.java)
    }

    fun storeExitHotKey(hotKey: HotKey) {
        ConfigUtil.putObject(ConfigEnum.EXIT_HOT_KEY, hotKey)
    }

    fun getPauseHotKey(): HotKey? {
        return ConfigUtil.getObject(ConfigEnum.PAUSE_HOT_KEY, HotKey::class.java)
    }

    fun storePauseHotKey(hotKey: HotKey) {
        ConfigUtil.putObject(ConfigEnum.PAUSE_HOT_KEY, hotKey)
    }

    fun getWorkDay(): MutableList<WorkDay>{
        return ConfigUtil.getArray(ConfigEnum.WORK_DAY, WorkDay::class.java)?:mutableListOf()
    }

    fun storeWorkDay(workDays: List<WorkDay>){
        ConfigUtil.putArray(ConfigEnum.WORK_DAY, workDays)
        WorkListener.checkWork()
    }

    fun getWorkTime(): MutableList<WorkTime>{
        return ConfigUtil.getArray(ConfigEnum.WORK_TIME, WorkTime::class.java)?:mutableListOf()
    }

    fun storeWorkTime(workTime: List<WorkTime>){
        workTime.forEach {
            val parseStartTime = it.parseStartTime()
            val parseEndTime = it.parseEndTime()
            if (parseStartTime != null && parseEndTime != null) {
                if (parseEndTime.isBefore(parseStartTime)) {
                    it.endTime = it.startTime
                }
            }
        }
        ConfigUtil.putArray(ConfigEnum.WORK_TIME, workTime)
        WorkListener.checkWork()
    }

    fun getDeckPluginDisabled(): MutableList<String>{
        return ConfigUtil.getArray(ConfigEnum.DECK_PLUGIN_DISABLED, String::class.java)?:mutableListOf()
    }

    fun storeDeckPluginDisabled(disabledList: List<String>){
        ConfigUtil.putArray(ConfigEnum.DECK_PLUGIN_DISABLED, disabledList)
    }

    fun getCardPluginDisabled(): MutableList<String>{
        return ConfigUtil.getArray(ConfigEnum.CARD_PLUGIN_DISABLED, String::class.java)?:mutableListOf()
    }

    fun storeCardPluginDisabled(disabledList: List<String>){
        ConfigUtil.putArray(ConfigEnum.CARD_PLUGIN_DISABLED, disabledList)
    }

}