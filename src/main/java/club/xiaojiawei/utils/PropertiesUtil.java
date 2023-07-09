package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
}
