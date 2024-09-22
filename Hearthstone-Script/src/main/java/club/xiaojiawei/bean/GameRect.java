package club.xiaojiawei.bean;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.Getter;
import lombok.ToString;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private void cancel(){
        GameUtil.cancelAction();
        SystemUtil.delayTiny();
    }

    public boolean isValid() {
        return !Objects.equals(this, INVALID);
    }

    public void lClick() {
        lClick(true);
    }

    public void lClick(boolean isCancel) {
        if (isCancel) cancel();
        GameUtil.leftButtonClick(getClickPos());
    }

    public void rClick() {
        GameUtil.rightButtonClick(getClickPos());
    }

    public void lClickMoveLClick(GameRect endRect) {
        lClickMoveLClick(endRect, true);
    }

    public void lClickMoveLClick(GameRect endRect, boolean isCancel) {
        if (endRect == null) {
            return;
        }
        if (isCancel) cancel();
        Point startPos = getClickPos();
        Point endPos = endRect.getClickPos();
        GameUtil.leftButtonClick(startPos);
        SystemUtil.delay(100);
        GameUtil.moveMouse(startPos, endPos);
        SystemUtil.delay(100);
        GameUtil.leftButtonClick(endPos);
    }

    public void move() {
        GameUtil.moveMouse(getClickPos());
    }

    public void move(GameRect endRect) {
        if (endRect == null) {
            move();
        } else {
            GameUtil.moveMouse(getClickPos(), endRect.getClickPos());
        }
    }

    public Action buildAction() {
        return new Action(this);
    }

    public static class Action {

        private final GameRect rect;

        private final List<Runnable> runnableList = new ArrayList<>();

        private GameRect lastRect;

        private Action(GameRect rect) {
            this.rect = rect;
        }

        public Action clear() {
            runnableList.clear();
            lastRect = null;
            return this;
        }

        public Action exec() {
            runnableList.forEach(Runnable::run);
            return this;
        }

        public Action lClick() {
            runnableList.add(rect::lClick);
            return this;
        }

        public Action lClick(final GameRect rect) {
            runnableList.add(() -> {
                if (rect == null) {
                    if (lastRect != null) {
                        lastRect.lClick();
                    }
                } else {
                    rect.lClick();
                    lastRect = rect;
                }
            });
            return this;
        }

        public Action rClick() {
            runnableList.add(rect::rClick);
            return this;
        }

        public Action rClick(final GameRect rect) {
            runnableList.add(() -> {
                if (rect == null) {
                    if (lastRect != null) {
                        lastRect.rClick();
                    }
                } else {
                    rect.rClick();
                    lastRect = rect;
                }
            });
            return this;
        }

        public Action move() {
            runnableList.add(rect::move);
            return this;
        }

        public Action move(final GameRect endRect) {
            runnableList.add(() -> rect.move(endRect));
            return this;
        }

    }
}
