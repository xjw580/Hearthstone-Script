package club.xiaojiawei.initializer;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;

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

    @Override
    public void exec() {
        if (Strings.isNotBlank(scriptProperties.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey())) && Strings.isNotBlank(scriptProperties.getProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey()))){
            ScriptStaticData.setSetPath(true);
            if (nextInitializer != null){
                nextInitializer.init();
            }
        }else {
            log.error("炉石传说或战网安装路径未正确配置，脚本无法运行");
        }
    }
}
