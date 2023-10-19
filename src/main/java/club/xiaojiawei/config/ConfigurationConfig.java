package club.xiaojiawei.config;

import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationEnum;
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
 * 脚本配置文件
 * @author 肖嘉威
 * @date 2023/7/4 14:12
 */
@Configuration
@Slf4j
public class ConfigurationConfig {

    @Resource
    private SpringData springData;

    /**
     * 脚本配置文件
     */
    @Bean
    public Properties scriptConfiguration(){
        Properties properties = new Properties();
        reloadScriptProperties(properties);
        return properties;
    }

    private void reloadScriptProperties(Properties properties){
        File scriptConfigurationFile = new File(springData.getScriptConfigurationFile());
        if (!scriptConfigurationFile.exists()){
            new File(springData.getScriptPath()).mkdir();
            try(FileWriter fileWriter = new FileWriter(scriptConfigurationFile)){
                writeDefaultScriptProperties(fileWriter, properties);
                log.info("已创建脚本配置文件，路径：{}", scriptConfigurationFile.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try(FileReader fileReader = new FileReader(scriptConfigurationFile)){
            properties.load(fileReader);
            try(FileWriter fileWriter = new FileWriter(scriptConfigurationFile, true)){
                writeDefaultScriptProperties(fileWriter, properties);
            }
            properties.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDefaultScriptProperties(FileWriter fileWriter, Properties properties){
        for (ConfigurationEnum configurationEnum : ConfigurationEnum.values()) {
            if (!properties.containsKey(configurationEnum.getKey())){
                try {
                    fileWriter.write(configurationEnum.getKey() + "=" + configurationEnum.getDefaultValue() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
