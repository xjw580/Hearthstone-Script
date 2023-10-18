package club.xiaojiawei.utils;

import club.xiaojiawei.custom.dll.User32Dll;
import club.xiaojiawei.data.ScriptStaticData;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 110:18
 * @msg 鼠标工具类
 */
@Slf4j
@Component
public class MouseUtil {

    /**
     * 鼠标每次移动后的间隔时间
     */
    private static final int MOVE_INTERVAL = 7;
    /**
     * 鼠标每次移动的距离
     */
    private static final int MOVE_DISTANCE = 10;
    @Resource
    private AtomicReference<BooleanProperty> isPause;


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
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
            ScriptStaticData.ROBOT.mouseMove(startX, startY);
            SystemUtil.delay(100);
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delayShort();
            for (int i = 0; i < 50; i++) {
                ScriptStaticData.ROBOT.mouseMove(startX, --startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
            SystemUtil.delayShort();
            moveMouseByLine(startX, startY, endX, endY);
            SystemUtil.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
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
            startX = transformScalePixelX(startX);
            startY = transformScalePixelY(startY);
            endX = transformScalePixelX(endX);
            endY = transformScalePixelY(endY);
            ScriptStaticData.ROBOT.mouseMove(startX, startY);
            SystemUtil.delay(100);
            moveMouseByLine(startX, startY, endX, endY);
            SystemUtil.delayShort();
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(300);
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
            x = transformScalePixelX(x);
            y = transformScalePixelY(y);
            ScriptStaticData.ROBOT.mouseMove(x, y);
            SystemUtil.delay(100);
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            SystemUtil.delay(200);
        }
    }
    public void leftButtonClick(int[] pos){
        leftButtonClick(pos[0], pos[1]);
    }
    public void leftButtonClickByUser32(WinDef.HWND hwnd, int x, int y){
        if (isPause.get().get()){
            return;
        }
        User32Dll.INSTANCE.leftClick(hwnd, x, y);
    }

    /**
     * 转换成应用显示器缩放后的值
     * @param pixelX
     * @return
     */
    public static int transformScalePixelX(int pixelX){
        return (int) (pixelX / ScriptStaticData.DISPLAY_SCALE_X);
    }
    public static int transformScalePixelY(int pixelY){
        return (int) (pixelY / ScriptStaticData.DISPLAY_SCALE_Y);
    }

    /**
     * 炉石里点击右键可取消操作
     */
    public static void cancel(){
        SystemUtil.delay(1000);
        ScriptStaticData.ROBOT.mousePress(BUTTON3_DOWN_MASK);
        SystemUtil.delay(200);
        ScriptStaticData.ROBOT.mouseRelease(BUTTON3_DOWN_MASK);
    }

    /**
     * 计算斜率
     * @return double 斜率
     */
    private double calcK(int startX, int startY, int endX, int endY){
        return (double)(startY - endY) / (startX - endX);
    }

    /**
     * 鼠标按照直线方式移动
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private void moveMouseByLine(int startX, int startY, int endX, int endY){
        if (Math.abs(startY - endY) <= 5){
            for (startX -= MOVE_DISTANCE; startX >= endX; startX -= MOVE_DISTANCE){
                ScriptStaticData.ROBOT.mouseMove(startX, startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
        }else {
            double k = calcK(startX, startY, endX, endY);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ScriptStaticData.ROBOT.mouseMove((int) ((startY - b) / k), startY);
                SystemUtil.delay(MOVE_INTERVAL);
            }
        }
    }

}
