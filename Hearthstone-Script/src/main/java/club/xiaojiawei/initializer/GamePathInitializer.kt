package club.xiaojiawei.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.enums.ConfigEnum
import club.xiaojiawei.enums.RegCommonNameEnum
import club.xiaojiawei.utils.ConfigExUtil
import club.xiaojiawei.utils.ConfigUtil
import club.xiaojiawei.utils.SystemUtil
import org.apache.logging.log4j.util.Strings
import java.util.*

/**
 * 获取炉石和战网安装路径
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
object GamePathInitializer : AbstractInitializer() {

    public override fun exec() {
        var platformInstallLocation: String?
        var gameInstallLocation: String?
        if (ConfigUtil.getString(ConfigEnum.PLATFORM_PATH).isBlank()) {
            log.info {
                String.format(
                    "未配置%s安装路径，尝试从注册表读取",
                    ScriptStaticData.PLATFORM_CN_NAME
                )
            }
            if (Strings.isNotBlank(
                    SystemUtil.registryGetStringValueForUserProgram(
                        RegCommonNameEnum.INSTALL_LOCATION,
                        ScriptStaticData.PLATFORM_US_NAME
                    ).also { platformInstallLocation = it })
            ) {
                log.info { String.format("从注册表读取到%s安装路径", ScriptStaticData.PLATFORM_CN_NAME) }
                if (!ConfigExUtil.storePlatformPath(platformInstallLocation)) {
                    log.warn {
                        String.format(
                            "从注册表读取的%s安装路径无效",
                            ScriptStaticData.PLATFORM_CN_NAME
                        )
                    }
                    ScriptStaticData.setSetPath(false)
                }
            } else {
                log.warn {
                    String.format(
                        "%s安装路径读取失败，脚本无法正常运行",
                        ScriptStaticData.PLATFORM_CN_NAME
                    )
                }
                ScriptStaticData.setSetPath(false)
            }
        }
        if (ConfigUtil.getString(ConfigEnum.GAME_PATH).isBlank()) {
            log.info {
                String.format("未配置%s安装路径，尝试从注册表读取", ScriptStaticData.GAME_CN_NAME)
            }
            if (Strings.isNotBlank(
                    SystemUtil.registryGetStringValueForUserProgram(
                        RegCommonNameEnum.INSTALL_LOCATION,
                        ScriptStaticData.GAME_US_NAME
                    ).also { gameInstallLocation = it })
            ) {
                log.info { String.format("从注册表读取到%s安装路径", ScriptStaticData.GAME_CN_NAME) }
                if (!ConfigExUtil.storeGamePath(gameInstallLocation)) {
                    log.warn {
                        String.format(
                            "从注册表读取的%s安装路径无效",
                            ScriptStaticData.GAME_CN_NAME
                        )
                    }
                    ScriptStaticData.setSetPath(false)
                }
            } else {
                log.warn {
                    String.format(
                        "%s安装路径读取失败，脚本无法正常运行",
                        ScriptStaticData.GAME_CN_NAME
                    )
                }
                ScriptStaticData.setSetPath(false)
            }
        }
    }

}
