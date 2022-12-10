package club.xiaojiawei.hearthstone.utils;


import lombok.SneakyThrows;

import java.io.*;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 16:04
 */
public class PropertiesUtil {

    @SneakyThrows
    public static Properties getProperties(String path, String name){
        Properties properties = new Properties();
        File file = new File(path + name);
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            properties.load(fileInputStream);
        }
        return properties;
    }
}
