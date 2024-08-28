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
@Component
public class MouseUtil {

    /**
     * 鼠标每次移动后的间隔时间：ms
     */
    private static final int MIN_MOVE_INTERVAL = 1;
    /**
     * 鼠标每次移动的距离：px
     */
    private static final int MOVE_DISTANCE = 10;
    private static Properties scriptConfiguration;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private static double initX;
    private static double initY;

    @Resource
    public void setScriptConfiguration(Properties scriptConfiguration) {
        MouseUtil.scriptConfiguration = scriptConfiguration;
    }

    private static int getMaxMoveInterval() {
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
    public void leftButtonDrag(int startX, int startY, int endX, int endY) {
        if (isPause.get().get()) {
            return;
        }
        synchronized (MouseUtil.class) {
            saveInitPos();
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
//            ROBOT.mouseMove(startX, startY);
            SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
            delayShort();
//            ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemDll.INSTANCE.leftClick(startX, startY, getGameHWND());
            SystemUtil.delayShort();
            for (int i = 0; i < 50; i++) {
//                ROBOT.mouseMove(startX, --startY);
                SystemDll.INSTANCE.moveMouse(startX, --startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
            SystemUtil.delayShort();
            moveMouseByLine(startX, startY, endX, endY);
            delayShort();
//            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemDll.INSTANCE.moveMouse(endX, endY, getGameHWND());
            delayShort();
            gotoInitPos();
        }
    }

    public void leftButtonDrag(int[] start, int[] end) {
        leftButtonDrag(start[0], start[1], end[0], end[1]);
    }


    /**
     * 鼠标左键从指定处移动到指定处然后点击
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void leftButtonMoveThenClick(int startX, int startY, int endX, int endY) {
        if (isPause.get().get()) {
            return;
        }
        synchronized (MouseUtil.class) {
            saveInitPos();
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
//            ROBOT.mouseMove(startX, startY);
            SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
            delayShort();
            moveMouseByLine(startX, startY, endX, endY);
            SystemUtil.delayShort();
//            ROBOT.mousePress(BUTTON1_DOWN_MASK);
//            delayShort();
//            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemDll.INSTANCE.leftClick(endX, endY, getGameHWND());
            delayShort();
            gotoInitPos();
        }
    }

    public void leftButtonMoveThenClick(int[] start, int[] end) {
        leftButtonMoveThenClick(start[0], start[1], end[0], end[1]);
    }

    public void leftButtonClick(float horizontalToCenterRation, float verticalToBottomRation, int[] xRandom, int[] yRandom) {
        if (xRandom == null || xRandom.length < 2) {
            xRandom = new int[]{0, 0};
        }
        if (yRandom == null || yRandom.length < 2) {
            yRandom = new int[]{0, 0};
        }
        int centerX = (GAME_RECT.right + GAME_RECT.left) >> 1;
        int windowHeight = GAME_RECT.bottom - GAME_RECT.top;
        float windowWidth = windowHeight * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        float width = windowWidth * horizontalToCenterRation;
        int x = (int) (centerX + width + RandomUtil.getRandom(xRandom[0], xRandom[1]));
        float height = windowHeight * verticalToBottomRation;
        int y = (int) (ScriptStaticData.GAME_RECT.bottom - height) + RandomUtil.getRandom(yRandom[0], yRandom[1]);
        leftButtonClick(x, y);
    }

    /**
     * 鼠标左键点击指定处
     *
     * @param x
     * @param y
     */
    public void leftButtonClick(int x, int y) {
        if (isPause.get().get()) {
            return;
        }
        synchronized (MouseUtil.class) {
            saveInitPos();
            x = transformScalePixelX(x);
            y = transformScalePixelY(y);
//            User32.INSTANCE.SetCursorPos(x, y);
            SystemDll.INSTANCE.leftClick(x, y, getGameHWND());
//            ROBOT.mouseMove(x, y);
//            delayShort();
//            ROBOT.mousePress(BUTTON1_DOWN_MASK);
//            delayShort();
//            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
//            delayShort();
            gotoInitPos();
        }
    }

    public void leftButtonClick(int[] pos) {
        leftButtonClick(pos[0], pos[1]);
    }

    public void leftButtonClickByUser32(WinDef.HWND hwnd, int x, int y) {
        if (isPause.get().get()) {
            return;
        }
        SystemDll.INSTANCE.leftClick(x, y, hwnd);
    }

    private void saveInitPos() {
        initX = MouseInfo.getPointerInfo().getLocation().getX();
        initY = MouseInfo.getPointerInfo().getLocation().getY();
    }

    private void gotoInitPos() {
        if (Objects.equals(scriptConfiguration.getProperty(ConfigurationEnum.STATIC_CURSOR.getKey()), "true")) {
            ROBOT.mouseMove((int) initX, (int) initY);
        }
    }

    /**
     * 转换成应用显示器缩放后的值
     *
     * @param pixelX
     * @return
     */
    public static int transformScalePixelX(int pixelX) {
        return (int) (pixelX / DISPLAY_SCALE_X);
    }

    public static int transformScalePixelY(int pixelY) {
        return (int) (pixelY / DISPLAY_SCALE_Y - WINDOW_TITLE_PIXEL_Y);
    }

    /**
     * 炉石里点击右键可取消操作
     */
    public static void gameCancel() {
        SystemUtil.delay(250, 500);
        SystemDll.INSTANCE.rightClick((int) initX, (int) initY, getGameHWND());
//        ROBOT.mousePress(BUTTON3_DOWN_MASK);
//        delayShort();
//        ROBOT.mouseRelease(BUTTON3_DOWN_MASK);
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
//                ROBOT.mouseMove(startX, startY);
                SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else if (Math.abs(startX - endX) <= 5) {
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE) {
//                ROBOT.mouseMove(startX, startY);
                SystemDll.INSTANCE.moveMouse(startX, startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        } else {
            double k = calcK(startX, startY, endX, endY);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE) {
//                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                SystemDll.INSTANCE.moveMouse((int) ((startY - b) / k), startY, getGameHWND());
                SystemUtil.delay(MIN_MOVE_INTERVAL, getMaxMoveInterval());
            }
        }
        ROBOT.mouseMove(endX, endY);
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
