package club.xiaojiawei.bean;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;

import java.awt.*;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 15:15
 */
public record GameRect(double left, double right, double top, double bottom) {

    public static final GameRect INVALID = new GameRect(0, 0, 0, 0);

    public Point getClickPos() {
        int realH = ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top, usableH = realH;
        int realW = ScriptStaticData.GAME_RECT.right - ScriptStaticData.GAME_RECT.left;
        int usableW = (int) (realH * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO);
        int middleX = realW >> 1;
        int middleY = realH >> 1;
        double pointX = RandomUtil.getRandom(left, right);
        double pointY = RandomUtil.getRandom(top, bottom);
        return new Point((int) (middleX + pointX * usableW), (int) (middleY + pointY * usableH));
    }

    public void lClick() {
        GameUtil.leftButtonClick(getClickPos());
    }

    public void rClick() {
        GameUtil.rightButtonClick(getClickPos());
    }

    public void lClickMoveLClick(GameRect endRect) {
        if (endRect == null) {
            return;
        }
        Point startPos = getClickPos();
        Point endPos = endRect.getClickPos();
        GameUtil.leftButtonClick(startPos);
        GameUtil.moveMouseByLine(startPos, endPos);
        GameUtil.leftButtonClick(endPos);
    }
}
