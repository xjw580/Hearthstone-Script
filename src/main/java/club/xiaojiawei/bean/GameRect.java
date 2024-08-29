package club.xiaojiawei.bean;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 15:15
 */
@Data
@AllArgsConstructor
public class GameRect {

    public static final GameRect DEFAULT = new GameRect(0, 0, 0, 0);

    private final double left;

    private final double right;

    private final double top;

    private final double bottom;

    public Point getClickPos(){
        int realH = ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top, usableH = realH;
        int realW = ScriptStaticData.GAME_RECT.right - ScriptStaticData.GAME_RECT.left;
        int usableW = (int) (realH * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO);
        int middleX = realW >> 1;
        int middleY = realH >> 1;
        double pointX = RandomUtil.getRandom(left, right);
        double pointY = RandomUtil.getRandom(top, bottom);
        return new Point((int) (middleX + pointX * usableW), (int) (middleY + pointY * usableH));
    }

    public void click(){
        MouseUtil.leftButtonClick(getClickPos());
    }

    public void clickMoveClick(GameRect gameRect){
        if (gameRect == null) {
            return;
        }
        MouseUtil.leftButtonDrag(getClickPos(), gameRect.getClickPos());
    }
}
