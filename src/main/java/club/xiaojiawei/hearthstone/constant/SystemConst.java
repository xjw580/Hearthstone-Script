package club.xiaojiawei.hearthstone.constant;

import club.xiaojiawei.hearthstone.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 22:48
 */
@Component
public class SystemConst {


    public static Properties PROPERTIES;
    @Value("${game.properties.path}")
    public void setProperties(String path){
        SystemConst.PROPERTIES = PropertiesUtil.getProperties(path);
    }
    public static final Robot ROBOT;

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}
