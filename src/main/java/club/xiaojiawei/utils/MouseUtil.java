package club.xiaojiawei.utils;

import club.xiaojiawei.custom.dll.SystemDll;
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
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

/**
 * 鼠标工具类
 * @author 肖嘉威
 * @date 2022/11/24 11:18
 */
@Slf4j
@Component
public class MouseUtil {

    /**
     * 鼠标每次移动后的间隔时间：ms
     */
    private static final int MOVE_INTERVAL = 7;
    /**
     * 鼠标每次移动的距离：px
     */
    private static final int MOVE_DISTANCE = 10;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private Properties scriptConfiguration;
    private double initX;
    private double initY;

    /**
     * 鼠标左键从指定处拖拽到指定处
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void leftButtonDrag(int startX, int startY, int endX, int endY) {
        if (isPause.get().get()){
            return;
        }
        synchronized (MouseUtil.class){
            saveInitPos();
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
            ROBOT.mouseMove(startX, startY);
            SystemUtil.delay(100);
            ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delayShort();
            for (int i = 0; i < 50; i++) {
                ROBOT.mouseMove(startX, --startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
            SystemUtil.delayShort();
            moveMouseByLine(startX, startY, endX, endY);
            SystemUtil.delay(100);
            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
            gotoInitPos();
        }
    }
    public void leftButtonDrag(int[] start, int[] end) {
        leftButtonDrag(start[0], start[1], end[0], end[1]);
    }


    /**
     * 鼠标左键从指定处移动到指定处然后点击
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void leftButtonMoveThenClick(int startX, int startY, int endX, int endY){
        if (isPause.get().get()){
            return;
        }
        synchronized (MouseUtil.class){
            saveInitPos();
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
            ROBOT.mouseMove(startX, startY);
            SystemUtil.delay(100);
            moveMouseByLine(startX, startY, endX, endY);
            SystemUtil.delayShort();
            ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(200);
            gotoInitPos();
        }
    }
    public void leftButtonMoveThenClick(int[] start, int[] end) {
        leftButtonMoveThenClick(start[0], start[1], end[0], end[1]);
    }

    /**
     * 鼠标左键点击指定处
     * @param x
     * @param y
     */
    public void leftButtonClick(int x, int y){
        if (isPause.get().get()){
            return;
        }
        synchronized (MouseUtil.class){
            saveInitPos();
            x = transformScalePixelX(x);
            y = transformScalePixelY(y);
            ROBOT.mouseMove(x, y);
            SystemUtil.delay(100);
            ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(200);
            gotoInitPos();
        }
    }
    public void leftButtonClick(int[] pos){
        leftButtonClick(pos[0], pos[1]);
    }

    public void leftButtonClickByUser32(WinDef.HWND hwnd, int x, int y){
        if (isPause.get().get()){
            return;
        }
        SystemDll.INSTANCE.leftMouseClick(hwnd, x, y);
    }

    private void saveInitPos(){
        if (Objects.equals(scriptConfiguration.getProperty(ConfigurationEnum.STATIC_CURSOR.getKey()), "true")){
            initX = MouseInfo.getPointerInfo().getLocation().getX();
            initY = MouseInfo.getPointerInfo().getLocation().getY();
        }
    }
    private void gotoInitPos(){
        if (Objects.equals(scriptConfiguration.getProperty(ConfigurationEnum.STATIC_CURSOR.getKey()), "true")){
            ROBOT.mouseMove((int) initX, (int) initY);
        }
    }

    /**
     * 转换成应用显示器缩放后的值
     * @param pixelX
     * @return
     */
    public static int transformScalePixelX(int pixelX){
        return (int) (pixelX / DISPLAY_SCALE_X);
    }
    public static int transformScalePixelY(int pixelY){
        return (int) (pixelY / DISPLAY_SCALE_Y);
    }

    /**
     * 炉石里点击右键可取消操作
     */
    public static void gameCancel(){
        SystemUtil.delay(1000);
        ROBOT.mousePress(BUTTON3_DOWN_MASK);
        SystemUtil.delay(200);
        ROBOT.mouseRelease(BUTTON3_DOWN_MASK);
    }

    /**
     * 计算斜率
     * @return double 斜率
     */
    private static double calcK(int startX, int startY, int endX, int endY){
        return (double)(startY - endY) / (startX - endX);
    }

    /**
     * 鼠标按照直线方式移动
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private static void moveMouseByLine(int startX, int startY, int endX, int endY){
        if (Math.abs(startY - endY) <= 5){
            for (startX -= MOVE_DISTANCE; startX >= endX; startX -= MOVE_DISTANCE){
                ROBOT.mouseMove(startX, startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
        }else if (Math.abs(startX - endX) <= 5){
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ROBOT.mouseMove(startX, startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
        }else {
            double k = calcK(startX, startY, endX, endY);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
        }
        ROBOT.mouseMove(endX, endY);
        SystemUtil.delay(MOVE_INTERVAL);
    }
    /**
     * 鼠标按照贝塞尔曲线方式移动
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private static void moveMouseByCurve(int startX, int startY, int endX, int endY){
    }
}
