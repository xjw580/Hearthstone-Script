package club.xiaojiawei.hearthstone.utils;

import club.xiaojiawei.hearthstone.run.Core;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.hearthstone.constant.SystemConst.*;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 110:18
 */
@Slf4j
public class MouseUtil {

    private static final int MOVE_INTERVAL = 6;
    private static final int MOVE_DISTANCE = 10;

    /**
     * 鼠标左键从指定处拖拽到指定处
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public synchronized static void leftButtonDrag(int startX, int startY, int endX, int endY) {
        if (Core.getPause()){
            return;
        }
        startX = pixelToPosX(startX);
        startY = pixelToPosY(startY);
        endX = pixelToPosX(endX);
        endY = pixelToPosY(endY);
        ROBOT.mouseMove(startX, startY);
        ROBOT.delay(100);
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        SystemUtil.delayShort();
        for (int i = 0; i < 50; i++) {
            ROBOT.mouseMove(startX, --startY);
            ROBOT.delay(MOVE_INTERVAL);
        }
        SystemUtil.delayShort();
        if (startX == endX){
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ROBOT.mouseMove(startX, startY);
                ROBOT.delay(MOVE_INTERVAL);
            }
        }else {
            double k = (double)(startY - endY) / (startX - endX);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                ROBOT.delay(MOVE_INTERVAL);
            }
        }
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(300);
    }

    /**
     * 鼠标左键从指定处移动到指定处然后点击
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public synchronized static void leftButtonMoveThenClick(int startX, int startY, int endX, int endY){
        if (Core.getPause()){
            return;
        }
        startX = pixelToPosX(startX);
        startY = pixelToPosY(startY);
        endX = pixelToPosX(endX);
        endY = pixelToPosY(endY);
        ROBOT.mouseMove(startX, startY);
        ROBOT.delay(100);
        if (Math.abs(startY - endY) < 20){
            for (startX -= MOVE_DISTANCE; startX >= endX; startX -= MOVE_DISTANCE){
                ROBOT.mouseMove(startX, startY);
                ROBOT.delay(MOVE_INTERVAL);
            }
        }else {
            double k = (double)(startY - endY) / (startX - endX);
            double b = startY - k * startX;
            for (startY -= MOVE_DISTANCE; startY >= endY; startY -= MOVE_DISTANCE){
                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                ROBOT.delay(MOVE_INTERVAL);
            }
        }
        SystemUtil.delayShort();
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(300);
    }

    /**
     * 鼠标左键点击指定处
     * @param x
     * @param y
     */
    public synchronized static void leftButtonClick(int x, int y){
        if (Core.getPause()){
            return;
        }
        x = pixelToPosX(x);
        y = pixelToPosY(y);
        ROBOT.mouseMove(x, y);
        ROBOT.delay(100);
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(200);
    }

    public static int pixelToPosX(int pixelX){
        return (int) (pixelX / UI_SCALE_X);
    }
    public static int pixelToPosY(int pixelY){
        return (int) (pixelY / UI_SCALE_Y);
    }
}
