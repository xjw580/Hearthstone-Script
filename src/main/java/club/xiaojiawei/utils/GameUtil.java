package club.xiaojiawei.utils;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.GameStaticData;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
@Component
@Slf4j
public class GameUtil {
    @Resource
    private MouseUtil mouseUtil;
    @Resource
    private SystemUtil systemUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private static ScheduledFuture<?> clickGameEndPageTask;

    public void clickBackButton(){
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + ((ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameStaticData.BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO) + RandomUtil.getRandom(-5, 5)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameStaticData.BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-2, 2)
        );
    }

    /**
     * 游戏里投降
     */
    public void surrender(){
        SystemUtil.stopAllThread();
        ScriptStaticData.ROBOT.delay(10000);
//        按ESC键弹出投降界面
        ScriptStaticData.ROBOT.keyPress(27);
        ScriptStaticData.ROBOT.keyRelease(27);
        ScriptStaticData.ROBOT.delay(2000);
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
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
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
        clickGameEndPageTask = extraThreadPool.scheduleWithFixedDelay(
                new LogRunnable(() -> {
                    if (isPause.get().get()){
                        cancelTask();
                    }
                    mouseUtil.leftButtonClick(
                            ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left >> 1,
                            (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                    );
                }),
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    public static void cancelTask(){
        if (clickGameEndPageTask != null && !clickGameEndPageTask.isDone()){
            log.info("点掉游戏结束结算页面任务已取消");
            clickGameEndPageTask.cancel(true);
        }
    }

    private static final float SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.652;
}
