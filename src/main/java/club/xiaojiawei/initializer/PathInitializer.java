package club.xiaojiawei.initializer;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.RegCommonNameEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Properties;

import static club.xiaojiawei.utils.SystemUtil.registryGetStringValueForUserProgram;

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 * @msg 检查炉石和战网安装路径是否设置
 */
@Component
@Slf4j
public class PathInitializer extends AbstractInitializer{

    @Resource
    private Properties scriptProperties;
    @Resource
    private PropertiesUtil propertiesUtil;

    @Override
    public void exec() {
        if (Strings.isNotBlank(scriptProperties.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey())) && Strings.isNotBlank(scriptProperties.getProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey()))){
            ScriptStaticData.setSetPath(true);
            if (nextInitializer != null){
                nextInitializer.init();
            }
        }else {
            String platformInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.PLATFORM_US_NAME), absolutePlatformPath, gameInstallLocation;
            if (Strings.isNotBlank(platformInstallLocation)
                    && new File(absolutePlatformPath = platformInstallLocation + "\\" + ScriptStaticData.PLATFORM_US_NAME + ".exe").exists()
                    && Strings.isNotBlank(gameInstallLocation = registryGetStringValueForUserProgram(RegCommonNameEnum.INSTALL_LOCATION, ScriptStaticData.GAME_US_NAME))
                    && propertiesUtil.storePath(gameInstallLocation, absolutePlatformPath)
            ){
                log.info("通过注册表获取到战网和炉石传说路径");
                ScriptStaticData.setSetPath(true);
                if (nextInitializer != null){
                    nextInitializer.init();
                }
            }else {
                log.error("炉石传说或战网安装路径未正确配置，脚本无法运行");
            }
        }
    }
}
