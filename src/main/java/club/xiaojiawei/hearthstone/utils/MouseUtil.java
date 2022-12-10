package club.xiaojiawei.hearthstone.utils;

import club.xiaojiawei.hearthstone.run.Core;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:18
 */
@Slf4j
public class MouseUtil {

    /**
     * 检测到人工移动后暂停时间
     */
    public static final int REST_TIME = 10;

    /**
     * 鼠标左键从指定处拖拽到指定处
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public synchronized static void leftButtonDrag(int startX, int startY, int endX, int endY) {
        ROBOT.mouseMove(startX, startY);
        ROBOT.delay(100);
        if (testArtificialMove(startX, startY)){
            return;
        }
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        SystemUtil.delayShort();
        for (int i = 0; i < 50; i++) {
            ROBOT.mouseMove(startX, --startY);
            ROBOT.delay(7);
        }
        SystemUtil.delayShort();
        if (startX == endX){
            for (startY -= 10; startY >= endY; startY -= 10){
                ROBOT.mouseMove(startX, startY);
                ROBOT.delay(7);
            }
        }else {
            double k = (double)(startY - endY) / (startX - endX);
            double b = startY - k * startX;
            for (startY -= 10; startY >= endY; startY -= 10){
                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                ROBOT.delay(7);
            }
        }
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(200);
    }

    /**
     * 鼠标左键从指定处移动到指定处然后点击
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public synchronized static void leftButtonMoveThenClick(int startX, int startY, int endX, int endY){
        ROBOT.mouseMove(startX, startY);
        ROBOT.delay(100);
        if (testArtificialMove(startX, startY)){
            return;
        }
        if (Math.abs(startY - endY) < 20){
            for (startX -= 10; startX >= endX; startX -= 10){
                ROBOT.mouseMove(startX, startY);
                ROBOT.delay(7);
            }
        }else {
            double k = (double)(startY - endY) / (startX - endX);
            double b = startY - k * startX;
            for (startY -= 10; startY >= endY; startY -= 10){
                ROBOT.mouseMove((int) ((startY - b) / k), startY);
                ROBOT.delay(7);
            }
        }
        SystemUtil.delayShort();
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(200);
    }

    /**
     * 鼠标左键点击指定处
     * @param x
     * @param y
     */
    public synchronized static void leftButtonClick(int x, int y){
        ROBOT.mouseMove(x, y);
        ROBOT.delay(100);
        if (testArtificialMove(x, y)){
            return;
        }
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        ROBOT.delay(100);
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
        ROBOT.delay(200);
    }

    /**
     * 检测是否有人工移动
     * @param x
     * @param y
     * @return
     */
    public static boolean testArtificialMove(int x, int y){
        Point location = MouseInfo.getPointerInfo().getLocation();
        if (Math.abs(x - location.x) > 30 || Math.abs(y - location.y) > 30){
            log.info("检测到人为鼠标移动，停止运行");
            SystemUtil.notice("检测到人为鼠标移动，暂时停止运行," + REST_TIME + "s后恢复");
            Core.setPause(true);
            for (long i = REST_TIME; i > 0; i -= 2) {
                log.info(i + "s后恢复运行");
                ROBOT.delay(2000);
            }
            log.info("已恢复");
            SystemUtil.notice("已恢复");
            Core.setPause(false);
            SystemUtil.frontWindow(Core.getGameHWND());
            leftButtonClick(x, y);
            return true;
        }
        return false;
    }

}
