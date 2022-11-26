package club.xiaojiawei.hearthstone.utils;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:18
 */
public class MouseUtil {


    /**
     * 鼠标左键拖拽
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public static void mouseLeftButtonDrag(int startX, int startY, int endX, int endY) throws InterruptedException {
        ROBOT.mouseMove(startX, startY);
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        ROBOT.delay(200);
        for (int i = 10; i <= 50; i += 10) {
            ROBOT.mouseMove(startX + RandomUtil.getRandom(-5, 5), startY - i);
            Thread.sleep(40);
        }
        startX += 50;
        ROBOT.delay(200);
        for (int i = 10; i <= 200; i += 10) {
            ROBOT.mouseMove(startX + RandomUtil.getRandom(-5, 5), startY - i);
            Thread.sleep(40);
        }
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
    }

    /**
     * 鼠标左键点击
     * @param x
     * @param y
     */
    public static void mouseLeftButtonClick(int x, int y){
        for (int i = 0; i < 2; i++) {
            ROBOT.mouseMove(x, y);
            ROBOT.mousePress(BUTTON1_DOWN_MASK);
            ROBOT.delay(RandomUtil.getRandom(160, 220));
            ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
            ROBOT.delay(RandomUtil.getRandom(80, 120));
        }
    }


}
