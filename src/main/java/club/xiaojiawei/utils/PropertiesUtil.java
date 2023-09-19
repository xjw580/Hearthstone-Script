package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2023/7/9 2:26
 */
@Component
public class PropertiesUtil {

    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptProperties;

    public void storeScriptProperties(){
        try(FileOutputStream fileOutputStream = new FileOutputStream(springData.getScriptConfigurationFile())){
            scriptProperties.store(fileOutputStream, ScriptStaticData.AUTHOR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean storeGamePath(String path){
        if (new File(path).exists()){
            if (!new File(path + "/" + ScriptStaticData.GAME_PROGRAM_NAME).exists()){
                return false;
            }
            scriptProperties.setProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey(), path);
            storeScriptProperties();
            return true;
        }
        return false;
    }
    public void storePlatformPath(String path){
        scriptProperties.setProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey(), path);
        storeScriptProperties();
    }
    public boolean storePath(String gamePath, String platformPath){
        storePlatformPath(platformPath);
        return storeGamePath(gamePath);
    }
}
