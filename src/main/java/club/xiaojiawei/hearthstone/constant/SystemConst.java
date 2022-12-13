package club.xiaojiawei.hearthstone.constant;

import club.xiaojiawei.hearthstone.listener.PowerFileListener;
import club.xiaojiawei.hearthstone.listener.ScreenFileListener;
import club.xiaojiawei.hearthstone.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2022/11/24 22:48
 */
@Component
public class SystemConst {

    @Resource
    private PowerFileListener powerFileListener;

    @Resource
    private ScreenFileListener screenFileListener;

    public static Properties PROPERTIES;

    private static String path;

    private static String name;
    /**
     * 炉石传说存放日志的文件夹
     */
    public static final String GAME_LOG_PATH_SUFFIX = "/Logs/";
    /**
     * 炉石程序名
     */
    public static final String GAME_PROGRAM_NAME = "Hearthstone.exe";
    /**
     * 暂停时间
     */
    public static final int REST_TIME = 15;
    public static String getPath() {
        return path;
    }
    public static String getName() {
        return name;
    }
    @Value("${script.main.path}")
    public void setPath(String path){
        SystemConst.path = path;
    }
    @Value("${script.main.name}")
    public void setName(String name){
        SystemConst.name = name;
    }
    @PostConstruct
    public void init(){
        SystemConst.PROPERTIES = PropertiesUtil.getProperties(path, name);
        powerFileListener.init();
        screenFileListener.init();
    }
    public static final Robot ROBOT;
    public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    /**
     * 显示器缩放
     */
    public static final double UI_SCALE_X;
    /**
     * 显示器缩放
     */
    public static final double UI_SCALE_Y;

    static {
        try {
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().
                    getDefaultConfiguration();
            AffineTransform tx = gc.getDefaultTransform();
            UI_SCALE_X = tx.getScaleX();
            UI_SCALE_Y = tx.getScaleY();
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

}
