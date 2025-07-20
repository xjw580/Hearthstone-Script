package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.GAME_US_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_US_NAME
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.RegCommonNameEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil

/**
 * 获取炉石和战网安装路径
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
class GamePathInitializer : AbstractInitializer() {

    public override fun exec() {
        var isValidGameInstallPath = ScriptStatus.isValidGameInstallPath
        var isValidPlatformProgramPath = ScriptStatus.isValidPlatformProgramPath

        val platformInstallLocation: String?
        if (ConfigUtil.getString(ConfigEnum.PLATFORM_PATH).isBlank()) {
            log.info {
                String.format(
                    "未配置%s程序路径，尝试从注册表读取",
                    PLATFORM_CN_NAME
                )
            }
            platformInstallLocation = SystemUtil.registryGetStringValueForUserProgram(
                RegCommonNameEnum.INSTALL_LOCATION,
                PLATFORM_US_NAME
            )
            if (!platformInstallLocation.isNullOrBlank()) {
                log.info { String.format("从注册表读取到%s程序路径", PLATFORM_CN_NAME) }
                if (!ConfigExUtil.storePlatformPath(platformInstallLocation)) {
                    log.warn {
                        String.format(
                            "从注册表读取的%s程序路径无效",
                            PLATFORM_CN_NAME
                        )
                    }
                    isValidPlatformProgramPath = false
                }
            } else {
                log.warn {
                    String.format(
                        "%s程序路径读取失败，脚本无法正常运行",
                        PLATFORM_CN_NAME
                    )
                }
                isValidPlatformProgramPath = false
            }
        }

        val gameInstallLocation: String?
        if (ConfigUtil.getString(ConfigEnum.GAME_PATH).isBlank()) {
            log.info {
                String.format("未配置%s安装路径，尝试从注册表读取", GAME_CN_NAME)
            }
            gameInstallLocation = SystemUtil.registryGetStringValueForUserProgram(
                RegCommonNameEnum.INSTALL_LOCATION,
                GAME_US_NAME
            )
            if (!gameInstallLocation.isNullOrBlank()) {
                log.info { String.format("从注册表读取到%s安装路径", GAME_CN_NAME) }
                if (!ConfigExUtil.storeGamePath(gameInstallLocation)) {
                    log.warn {
                        String.format(
                            "从注册表读取的%s安装路径无效",
                            GAME_CN_NAME
                        )
                    }
                    isValidGameInstallPath = false
                }
            } else {
                log.warn {
                    String.format(
                        "%s安装路径读取失败，脚本无法正常运行",
                        GAME_CN_NAME
                    )
                }
                isValidGameInstallPath = false
            }
        }
        ScriptStatus.isValidGameInstallPath = isValidGameInstallPath
        ScriptStatus.isValidPlatformProgramPath = isValidPlatformProgramPath
    }

}
