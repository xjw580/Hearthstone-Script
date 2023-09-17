package club.xiaojiawei.utils;

import club.xiaojiawei.custom.dll.User32Dll;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.Release;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 110:18
 */
@Slf4j
@Component
public class MouseUtil {

    private static final int MOVE_INTERVAL = 7;
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
            startX = pixelToScaleX(startX);
            startY = pixelToScaleY(startY);
            endX = pixelToScaleX(endX);
            endY = pixelToScaleY(endY);
            ScriptStaticData.ROBOT.mouseMove(startX, startY);
            ScriptStaticData.ROBOT.delay(100);
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            SystemUtil.delayShort();
            for (int i = 0; i < 50; i++) {
                ScriptStaticData.ROBOT.mouseMove(startX, --startY);
                ScriptStaticData.ROBOT.delay(MOVE_INTERVAL);
            }
            SystemUtil.delayShort();
            if (startX == endX){
                for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                    ScriptStaticData.ROBOT.mouseMove(startX, startY);
                    ScriptStaticData.ROBOT.delay(MOVE_INTERVAL);
                }
            }else {
                double k = (double)(startY - endY) / (startX - endX);
                double b = startY - k * startX;
                for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                    ScriptStaticData.ROBOT.mouseMove((int) ((startY - b) / k), startY);
                    ScriptStaticData.ROBOT.delay(MOVE_INTERVAL);
                }
            }
            ScriptStaticData.ROBOT.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            ScriptStaticData.ROBOT.delay(100);
        }
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
            startX = pixelToScaleX(startX);
            startY = pixelToScaleY(startY);
            endX = pixelToScaleX(endX);
            endY = pixelToScaleY(endY);
            ScriptStaticData.ROBOT.mouseMove(startX, startY);
            ScriptStaticData.ROBOT.delay(100);
            if (Math.abs(startY - endY) < 20){
                for (startX -= MOVE_DISTANCE; startX >= endX; startX -= MOVE_DISTANCE){
                    ScriptStaticData.ROBOT.mouseMove(startX, startY);
                    ScriptStaticData.ROBOT.delay(MOVE_INTERVAL);
                }
            }else {
                double k = (double)(startY - endY) / (startX - endX);
                double b = startY - k * startX;
                for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                    ScriptStaticData.ROBOT.mouseMove((int) ((startY - b) / k), startY);
                    ScriptStaticData.ROBOT.delay(MOVE_INTERVAL);
                }
            }
            SystemUtil.delayShort();
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            ScriptStaticData.ROBOT.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            ScriptStaticData.ROBOT.delay(300);
        }
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
            x = pixelToScaleX(x);
            y = pixelToScaleY(y);
            ScriptStaticData.ROBOT.mouseMove(x, y);
            ScriptStaticData.ROBOT.delay(100);
            ScriptStaticData.ROBOT.mousePress(BUTTON1_DOWN_MASK);
            ScriptStaticData.ROBOT.delay(100);
            ScriptStaticData.ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            ScriptStaticData.ROBOT.delay(200);
        }
    }
    public static final User32Dll USER32_DLL_INSTANCE = User32Dll.INSTANCE;
    public void leftButtonClick(WinDef.HWND hwnd, int x, int y){
        if (isPause.get().get()){
            return;
        }
        USER32_DLL_INSTANCE.leftClick(hwnd, x, y);
    }

    public static int pixelToScaleX(int pixelX){
        return (int) (pixelX / ScriptStaticData.DISPLAY_SCALE_X);
    }
    public static int pixelToScaleY(int pixelY){
        return (int) (pixelY / ScriptStaticData.DISPLAY_SCALE_Y);
    }
    public static void cancel(){
        SystemUtil.delayMedium();
//        点击右键
        ScriptStaticData.ROBOT.mousePress(BUTTON3_DOWN_MASK);
        ScriptStaticData.ROBOT.delay(200);
        ScriptStaticData.ROBOT.mouseRelease(BUTTON3_DOWN_MASK);
    }
}
