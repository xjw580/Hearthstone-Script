package club.xiaojiawei.utils;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 游戏工具类
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
@Component
@Slf4j
public class GameUtil {

    @Resource
    private MouseUtil mouseUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private static ScheduledFuture<?> clickGameEndPageTask;

    /**
     * 点击炉石里的返回按钮
     */
    public void clickBackButton(){
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + ((ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO) + RandomUtil.getRandom(-5, 5)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-2, 2)
        );
    }

    /**
     * 游戏里投降
     */
    public void surrender(){
        SystemUtil.stopAllThread();
        SystemUtil.delay(10000);
        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
//        按ESC键弹出投降界面
        ScriptStaticData.ROBOT.keyPress(27);
        ScriptStaticData.ROBOT.keyRelease(27);
        SystemUtil.delay(1500);
        SystemUtil.updateGameRect();
//        点击投降按钮
        mouseUtil.leftButtonClick(
            ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left >> 1,
            (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
        clickGameEndPageTask();
    }

    /**
     * 点掉游戏结束结算页面
     */
    public void clickGameEndPageTask(){
        SystemUtil.updateGameRect();
        cancelTask();
        log.info("点掉游戏结束结算页面");
        clickGameEndPageTask = extraThreadPool.scheduleWithFixedDelay(
            () -> {
                if (isPause.get().get()){
                    cancelTask();
                }else {
                    mouseUtil.leftButtonClick(
                            ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left >> 1,
                            (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                    );
                }
            },
            4500,
            2000,
            TimeUnit.MILLISECONDS
        );
    }

    public static void cancelTask(){
        if (clickGameEndPageTask != null && !clickGameEndPageTask.isDone()){
            clickGameEndPageTask.cancel(true);
        }
    }

    private static final float SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.652;
}
