package club.xiaojiawei.initializer;

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
        String platformInstallLocation, gameInstallLocation, absolutePlatformPath;
        if (Strings.isNotBlank(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()))
                && Strings.isNotBlank(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()))
        ){
            log.info("读取到战网和炉石传说路径");
        }else if (Strings.isNotBlank(platformInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.PLATFORM_US_NAME))
                && new File(absolutePlatformPath = platformInstallLocation + "\\" + ScriptStaticData.PLATFORM_US_NAME + ".exe").exists()
                && Strings.isNotBlank(gameInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.GAME_US_NAME))
                && propertiesUtil.storePath(gameInstallLocation, absolutePlatformPath)
        ){
            log.info("通过注册表获取到战网和炉石传说路径");

        }else {
            log.warn("炉石传说或战网安装路径未正确配置，脚本无法运行");
            return;
        }
        ScriptStaticData.setSetPath(true);
        if (nextInitializer != null){
            nextInitializer.init();
        }
    }
}
