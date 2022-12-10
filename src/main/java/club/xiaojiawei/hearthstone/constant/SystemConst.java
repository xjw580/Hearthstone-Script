package club.xiaojiawei.hearthstone.constant;

import club.xiaojiawei.hearthstone.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 22:48
 */
@Component
public class SystemConst {


    public static Properties PROPERTIES;

    private static String path;

    private static String name;

    public static String getPath() {
        return path;
    }

    public static String getName() {
        return name;
    }

    @Value("${game.properties.path}")
    public void setPath(String path){
        SystemConst.path = path;
    }

    @Value("${game.properties.name}")
    public void setName(String name){
        SystemConst.name = name;
    }

    @PostConstruct
    public void init(){
        SystemConst.PROPERTIES = PropertiesUtil.getProperties(path, name);
    }

    public static final Robot ROBOT;

    public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

}
