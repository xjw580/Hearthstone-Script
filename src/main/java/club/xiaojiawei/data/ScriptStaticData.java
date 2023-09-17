package club.xiaojiawei.data;

import com.sun.jna.platform.win32.WinDef;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author 肖嘉威
 * @date 2023/7/3 21:12
 * @msg 存储脚本内部变量
 */
@Slf4j
public class ScriptStaticData {

//    是否设置了炉石和战网的路径
    private static boolean isSetPath;
    public static final String GAME_CN_NAME = "炉石传说";
    public static final String PLATFORM_CN_NAME = "战网";
    public static final String GAME_US_NAME = "Hearthstone";
    public static final String PLATFORM_US_NAME = "Battle.net";
    public static final String REPO_NAME = "HearthstoneScript";
    public static final String TEMP_DIR = System.getProperty("user.dir") + "\\new_version_temp\\";
    /**
     * 游戏窗口句柄
     */
    @Getter
    private static WinDef.HWND gameHWND;
    /**
     * 平台窗口句柄
     */
    @Getter
    private static WinDef.HWND platformHWND;

    /**
     * 游戏窗口信息
     */
    public static final WinDef.RECT GAME_RECT = new WinDef.RECT();
    /**
     * 平台窗口信息
     */
    public static final WinDef.RECT PLATFORM_RECT = new WinDef.RECT();
    /**
     * 所有鼠标键盘模拟都需要此对象
     */
    public static final Robot ROBOT;
    /**
     * 显示器横向缩放
     */
    public static final double DISPLAY_SCALE_X;
    /**
     * 显示器纵向缩放
     */
    public static final double DISPLAY_SCALE_Y;
    /**
     * 显示器纵向像素数
     */
    public static final int DISPLAY_PIXEL_Y = Toolkit.getDefaultToolkit().getScreenSize().height;
    /**
     * 显示器横向像素数
     */
    public static final int DISPLAY_PIXEL_X = Toolkit.getDefaultToolkit().getScreenSize().width;
    /**
     * 窗口标题栏纵向像素数
     */
    public static final int WINDOW_TOP_PIXEL;

    /**
     * 本脚本的程序名
     */
    public static final String SCRIPT_NAME = "HS-Assistant";
    /**
     * 炉石传说程序名
     */
    public static final String GAME_NAME = "Hearthstone.exe";
    public static final String GAME_ALIVE_CMD = "cmd /c tasklist | find \"" + GAME_NAME + "\"";
    /**
     * 作者
     */
    public static final String AUTHOR = "zerg";
    public static final String MAIN_PATH = "/club/xiaojiawei/";
    /**
     * 图片路径
     */
    public static final String IMAGE_PATH = MAIN_PATH + "images/";
    /**
     * 脚本程序图标路径
     */
    public static final String SCRIPT_ICON_PATH = IMAGE_PATH + "main.png";


    public static boolean isSetPath() {
        return isSetPath;
    }

    public static void setSetPath(boolean setPath) {
        isSetPath = setPath;
    }

    public static void setGameHWND(WinDef.HWND gameHWND) {
        ScriptStaticData.gameHWND = gameHWND;
    }

    public static void setPlatformHWND(WinDef.HWND platformHWND) {
        ScriptStaticData.platformHWND = platformHWND;
    }

    static {
        try {
            ROBOT = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screenDevices.length > 1){
            log.info("检测到有多台显示器，请将炉石传说和战网放到主显示器运行");
        }
        AffineTransform tx = screenDevices[0].getDefaultConfiguration().getDefaultTransform();
        DISPLAY_SCALE_X = tx.getScaleX();
        DISPLAY_SCALE_Y = tx.getScaleY();
        WINDOW_TOP_PIXEL = (int) (33 / DISPLAY_SCALE_Y);
    }

}
