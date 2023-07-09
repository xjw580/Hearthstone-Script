package club.xiaojiawei.config;

import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.enums.DeckEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2023/7/4 14:12
 * @msg 各种properties对象
 */
@Configuration
@Slf4j
public class PropertiesConfig {

    @Resource
    private SpringData springData;

    /**
     * 脚本配置文件
     */
    @Bean
    public Properties scriptProperties(){
        Properties properties = new Properties();
        reloadScriptProperties(properties);
        return properties;
    }

    public void reloadScriptProperties(Properties properties){
        File scriptConfigurationFile = new File(springData.getScriptConfigurationFile());
        if (!scriptConfigurationFile.exists()){
            new File(springData.getScriptPath()).mkdir();
            try(FileWriter fileWriter = new FileWriter(scriptConfigurationFile)){
                for (ConfigurationKeyEnum configurationKeyEnum : ConfigurationKeyEnum.values()) {
                    fileWriter.write(configurationKeyEnum.getKey() + "=");
                    switch (configurationKeyEnum){
                        case RUN_MODE_KEY -> fileWriter.write(RunModeEnum.STANDARD.getValue() + "\n");
                        case DECK_KEY -> fileWriter.write(DeckEnum.FREE.getValue() + "\n");
                        default -> fileWriter.write("\n");
                    }
                }
                log.info("已创建脚本配置文件，路径：{}", scriptConfigurationFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(FileReader fileReader = new FileReader(scriptConfigurationFile)){
            properties.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
