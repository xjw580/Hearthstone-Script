package club.xiaojiawei.hearthstone.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 16:04
 */
public class PropertiesUtil {

    public static Properties getProperties(String path){
        Properties properties = new Properties();
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        ){
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
