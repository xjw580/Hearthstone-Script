package club.xiaojiawei.utils;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 16:04
 */
@Slf4j
public class PropertiesUtil {

    @SneakyThrows
    public static Properties getProperties(String path, String name){
        File pathFile = new File(path);
        if (!pathFile.exists() && !pathFile.mkdirs()){
            log.error(path + "路径不存在且创建失败");
        }
        Properties properties = new Properties();
        File file = new File(path + name);
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            properties.load(fileInputStream);
        }
        return properties;
    }
}
