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
    private static final Point lastPoint = new Point(-1, -1);

    private static boolean validPoint(Point point) {
        if (point == null) {
            return false;
        }
        return point.x != -1 && point.y != -1;
    }

    public static void leftButtonClick(WinDef.HWND hwnd) {
        leftButtonClick(lastPoint, hwnd);
    }
    public static void leftButtonClick(Point pos, WinDef.HWND hwnd) {
        if (!isPause.get().get() && validPoint(pos)) {
            SystemDll.INSTANCE.leftClick(pos.x, pos.y, hwnd);
            savePos(pos);
        }
    }

    public static void rightButtonClick(WinDef.HWND hwnd) {
        rightButtonClick(lastPoint, hwnd);
    }
    public static void rightButtonClick(Point pos, WinDef.HWND hwnd) {
        if (!isPause.get().get() && validPoint(pos)) {
            SystemDll.INSTANCE.rightClick(pos.x, pos.y, hwnd);
            savePos(pos);
        }
    }


    public static void moveMouseByLine(Point endPos, WinDef.HWND hwnd) {
        moveMouseByLine(null, endPos, hwnd);
    }

    /**
     * 鼠标按照直线方式移动
     */
    public static void moveMouseByLine(Point startPos, Point endPos, WinDef.HWND hwnd) {
        if (!isPause.get().get() && validPoint(endPos)) {
            int endX = endPos.x;
            int endY = endPos.y;
            if (validPoint(startPos)) {
                int startX = startPos.x;
                int startY = startPos.y;
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
            }
            SystemDll.INSTANCE.moveMouse(endX, endY, hwnd);
            savePos(endPos);
        }
    }

    private static void savePos(Point pos) {
        lastPoint.x = pos.x;
        lastPoint.y = pos.y;
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
