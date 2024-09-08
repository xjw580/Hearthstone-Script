package club.xiaojiawei.initializer;

import club.xiaojiawei.ScriptApplication;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.RegCommonNameEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

import static club.xiaojiawei.utils.SystemUtil.registryGetStringValueForUserProgram;

/**
 * 获取炉石和战网安装路径
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
@Component
@Slf4j
public class PathInitializer extends AbstractInitializer{

    @Resource
    private Properties scriptConfiguration;

    @Resource
    private PropertiesUtil propertiesUtil;

    @Override
    public void exec() {
        String platformInstallLocation, gameInstallLocation;
        if (Strings.isBlank(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()))){
            log.info(String.format("未配置%s安装路径，尝试从注册表读取", ScriptStaticData.PLATFORM_CN_NAME));
            if (Strings.isNotBlank(platformInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.PLATFORM_US_NAME))){
                log.info(String.format("从注册表读取到%s安装路径", ScriptStaticData.PLATFORM_CN_NAME));
                if (!propertiesUtil.storePlatformPath(platformInstallLocation)){
                    log.warn(String.format("从注册表读取的%s安装路径无效", ScriptStaticData.PLATFORM_CN_NAME));
                    ScriptStaticData.setSetPath(false);
                }
            }else {
                log.warn(String.format("%s安装路径读取失败，脚本无法正常运行", ScriptStaticData.PLATFORM_CN_NAME));
                ScriptStaticData.setSetPath(false);
            }
        }
        if (Strings.isBlank(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()))){
            log.info(String.format("未配置%s安装路径，尝试从注册表读取", ScriptStaticData.GAME_CN_NAME));
            if (Strings.isNotBlank(gameInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.GAME_US_NAME))){
                log.info(String.format("从注册表读取到%s安装路径", ScriptStaticData.GAME_CN_NAME));
                if (!propertiesUtil.storeGamePath(gameInstallLocation)){
                    log.warn(String.format("从注册表读取的%s安装路径无效", ScriptStaticData.GAME_CN_NAME));
                    ScriptStaticData.setSetPath(false);
                }
            }else {
                log.warn(String.format("%s安装路径读取失败，脚本无法正常运行", ScriptStaticData.GAME_CN_NAME));
                ScriptStaticData.setSetPath(false);
            }
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
