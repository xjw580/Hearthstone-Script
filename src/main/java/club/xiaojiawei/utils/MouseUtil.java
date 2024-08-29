package club.xiaojiawei.utils;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.ConfigurationEnum;
import com.sun.jna.platform.win32.WinDef;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.*;

/**
 * 鼠标工具类
 *
 * @author 肖嘉威
 * @date 2022/11/24 11:18
 */
@Slf4j
public class MouseUtil {

    /**
     * 鼠标每次移动后的间隔时间：ms
     */
    private static final int MIN_MOVE_INTERVAL = 1;
    /**
     * 鼠标每次移动的距离：px
     */
    private static final int MOVE_DISTANCE = 10;
    private static double lastX;
    private static double lastY;
    private static Properties scriptConfiguration;
    private static AtomicReference<BooleanProperty> isPause;

    public void init(Properties scriptConfiguration, AtomicReference<BooleanProperty> isPause) {
        MouseUtil.scriptConfiguration = scriptConfiguration;
        MouseUtil.isPause = isPause;
    }

    private static int getMaxMoveInterval() {
        if (scriptConfiguration == null) {
            return Integer.parseInt(ConfigurationEnum.MOUSE_MOVE_INTERVAL.getDefaultValue());
        }
        return Integer.parseInt(scriptConfiguration.getProperty(ConfigurationEnum.MOUSE_MOVE_INTERVAL.getKey(), ConfigurationEnum.MOUSE_MOVE_INTERVAL.getDefaultValue()));
    }

    /**
     * 鼠标左键从指定处拖拽到指定处
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public static void leftButtonDrag(int startX, int startY, int endX, int endY) {
        if (isPause.get().get()){
            return;
        }
        savePos(startX, startY);
        SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
        delayShort();
        SystemDll.INSTANCE.leftClick(startX, startY, getGameHWND());
        SystemUtil.delayShort();
        for (int i = 0; i < 50; i++) {
            SystemDll.INSTANCE.moveMouse(startX, --startY, getGameHWND());
            SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
        }
        SystemUtil.delayShort();
        moveMouseByLine(startX, startY, endX, endY);
        delayShort();
        SystemDll.INSTANCE.moveMouse(endX, endY, getGameHWND());
        delayShort();
        savePos(endX, endY);
    }

    public static void leftButtonDrag(int[] start, int[] end) {
        leftButtonDrag(start[0], start[1], end[0], end[1]);
    }

    public static void leftButtonDrag(Point start, Point end) {
        leftButtonDrag(start.x, start.y, end.x, end.y);
    }


    /**
     * 鼠标左键从指定处移动到指定处然后点击
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public static void leftButtonMoveThenClick(int startX, int startY, int endX, int endY) {
        if (isPause.get().get()){
            return;
        }
        savePos(startX, startY);
        SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
        delayShort();
        moveMouseByLine(startX, startY, endX, endY);
        SystemUtil.delayShort();
        SystemDll.INSTANCE.leftClick(endX, endY, getGameHWND());
        delayShort();
        savePos(endX, endY);
    }

    public static void leftButtonMoveThenClick(int[] start, int[] end) {
        leftButtonMoveThenClick(start[0], start[1], end[0], end[1]);
    }

    /**
     * 鼠标左键点击指定处
     * @param x
     * @param y
     */
    public static void leftButtonClick(int x, int y) {
        leftButtonClick(x, y, getGameHWND());
    }

    public static void leftButtonClick(int x, int y, WinDef.HWND hwnd) {
        if (isPause.get().get()){
            return;
        }
        SystemDll.INSTANCE.leftClick(x, y, hwnd);
        savePos(x, y);
    }

    public static void leftButtonClick(int[] pos) {
        leftButtonClick(pos[0], pos[1]);
    }

    public static void leftButtonClick(Point pos) {
        leftButtonClick(pos.x, pos.y);
    }

    private static void savePos(int x, int y) {
        lastX = x;
        lastY = y;
    }

    /**
     * 炉石里点击右键可取消操作
     */
    public static void gameCancel() {
        if (isPause.get().get()){
            return;
        }
        SystemUtil.delay(250, 500);
        SystemDll.INSTANCE.rightClick((int) lastX, (int) lastY, getGameHWND());
    }

    private static void delayShort() {
        SystemUtil.delay(30, 80);
    }

    /**
     * 计算斜率
     *
     * @return double 斜率
     */
    private static double calcK(int startX, int startY, int endX, int endY) {
        return (double) (startY - endY) / (startX - endX);
    }

    /**
     * 鼠标按照直线方式移动
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private static void moveMouseByLine(int startX, int startY, int endX, int endY) {
        if (Math.abs(startY - endY) <= 5) {
            for (startX -= MOVE_DISTANCE; startX >= endX; startX -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else if (Math.abs(startX - endX) <= 5) {
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else {
            double k = calcK(startX, startY, endX, endY);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE) {
                SystemDll.INSTANCE.moveMouse((int) ((startY - b) / k), startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        }
        SystemDll.INSTANCE.moveMouse(endX, endY, getGameHWND());
        SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
    }

    /**
     * 鼠标按照贝塞尔曲线方式移动
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private static void moveMouseByCurve(int startX, int startY, int endX, int endY) {
    }
}
