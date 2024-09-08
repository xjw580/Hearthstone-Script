package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationEnum;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 脚本配置工具类
 * @author 肖嘉威
 * @date 2023/7/9 2:26
 */
@Component
public class PropertiesUtil {

    @Resource
    private SpringData springData;
    @Resource
    @Getter
    private Properties scriptConfiguration;

    public void storeScriptProperties(){
        try(FileOutputStream fileOutputStream = new FileOutputStream(springData.getScriptConfigurationFile())){
            scriptConfiguration.store(fileOutputStream, ScriptStaticData.SCRIPT_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean storeGamePath(String gameInstallPath){
        if (new File(gameInstallPath + File.separator + ScriptStaticData.GAME_PROGRAM_NAME).exists()){
            scriptConfiguration.setProperty(ConfigurationEnum.GAME_PATH.getKey(), gameInstallPath);
            storeScriptProperties();
            return true;
        }
        return false;
    }

    public boolean storePlatformPath(String platformInstallPath){
        String programAbsolutePath;
        if (platformInstallPath != null && platformInstallPath.endsWith(".exe")){
            programAbsolutePath = platformInstallPath;
        }else {
            programAbsolutePath = platformInstallPath + File.separator + ScriptStaticData.PLATFORM_PROGRAM_NAME;
        }
        if (new File(programAbsolutePath).exists()){
            scriptConfiguration.setProperty(ConfigurationEnum.PLATFORM_PATH.getKey(), programAbsolutePath);
            storeScriptProperties();
            return true;
        }
        return false;
    }

}
