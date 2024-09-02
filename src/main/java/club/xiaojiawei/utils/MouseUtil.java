package club.xiaojiawei.utils;

import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.ConfigurationEnum;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 鼠标工具类
 * @author 肖嘉威
 * @date 2022/11/24 11:18
 */
@Slf4j
@Component
public class MouseUtil {

    private static Properties scriptConfiguration;

    private static AtomicReference<BooleanProperty> isPause = new AtomicReference<>(new SimpleBooleanProperty());

    public MouseUtil(Properties scriptConfiguration, AtomicReference<BooleanProperty> isPause) {
        MouseUtil.scriptConfiguration = scriptConfiguration;
        MouseUtil.isPause = isPause;
    }

    /**
     * 鼠标每次移动后的间隔时间：ms
     */
    private static final int MIN_MOVE_INTERVAL = 1;
    /**
     * 鼠标每次移动的距离：px
     */
    private static final int MOVE_DISTANCE = 10;
    @Getter
    private static int lastX;
    @Getter
    private static int lastY;

    public static void leftButtonClick(WinDef.HWND hwnd) {
        leftButtonClick(lastX, lastY, hwnd);
    }
    public static void leftButtonClick(Point point, WinDef.HWND hwnd) {
        leftButtonClick(point.x, point.y, hwnd);
    }
    public static void leftButtonClick(int x, int y, WinDef.HWND hwnd) {
        if (isPause.get().get()){
            return;
        }
        SystemDll.INSTANCE.leftClick(x, y, hwnd);
        savePos(x, y);
    }

    public static void rightButtonClick(WinDef.HWND hwnd) {
        rightButtonClick(lastX, lastY, hwnd);
    }
    public static void rightButtonClick(Point point, WinDef.HWND hwnd) {
        rightButtonClick(point.x, point.y, hwnd);
    }
    public static void rightButtonClick(int x, int y, WinDef.HWND hwnd) {
        if (isPause.get().get()){
            return;
        }
        SystemDll.INSTANCE.rightClick(x, y, hwnd);
        savePos(x, y);
    }


    public static void moveMouseByLine(Point startPos, Point endPos, WinDef.HWND hwnd) {
        moveMouseByLine(startPos.x, startPos.y, endPos.x, endPos.y, hwnd);
    }

    /**
     * 鼠标按照直线方式移动
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public static void moveMouseByLine(int startX, int startY, int endX, int endY, WinDef.HWND hwnd) {
        savePos(startX, startY);
        if (Math.abs(startY - endY) <= 5) {
            for (startX -= MOVE_DISTANCE; startX >= endX && !isPause.get().get(); startX -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse(startX, startY, hwnd);
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else if (Math.abs(startX - endX) <= 5) {
            for (startY -= MOVE_DISTANCE; startY >= endY && !isPause.get().get(); startY -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse(startX, startY, hwnd);
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else {
            double k = calcK(startX, startY, endX, endY);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY && !isPause.get().get(); startY -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse((int) ((startY - b) / k), startY, hwnd);
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        }
        SystemDll.INSTANCE.moveMouse(endX, endY, hwnd);
        savePos(endX, endY);
    }

    private static void savePos(int x, int y) {
        lastX = x;
        lastY = y;
    }

    /**
     * 计算斜率
     * @return double 斜率
     */
    private static double calcK(int startX, int startY, int endX, int endY) {
        return (double) (startY - endY) / (startX - endX);
    }


    private static int getMaxMoveInterval() {
        if (scriptConfiguration == null) {
            return Integer.parseInt(ConfigurationEnum.MOUSE_MOVE_INTERVAL.getDefaultValue());
        }
        return Integer.parseInt(scriptConfiguration.getProperty(ConfigurationEnum.MOUSE_MOVE_INTERVAL.getKey(), ConfigurationEnum.MOUSE_MOVE_INTERVAL.getDefaultValue()));
    }

}
