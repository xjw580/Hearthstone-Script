package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listener.PowerFileListener;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.AbstractDeckStrategy;
import club.xiaojiawei.hearthstone.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
@Slf4j
public class GameOverAbstractPhaseStrategy extends AbstractPhaseStrategy {

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.GAME_OVER_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        SystemUtil.delayMedium();
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
//        宣布本局游戏胜者，败者
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }
                break;
            }else if (PowerFileListener.isRelevance(l)){
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains("TAG_CHANGE")){
                    PowerLogUtil.dealTagChange(PowerLogUtil.parseTagChange(l));
                }
            }
        }
        startTimer();
        log.info(War.getCurrentPhase().getComment() + " -> 结束");
        log.info("已完成第 " + ++War.warCount + " 把游戏");
        AbstractDeckStrategy.setMyTurn(false);
        War.reset();
    }

    private static Timer timer;

    public static void startTimer(){
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MouseUtil.leftButtonClick((rect.right + rect.left) >> 1, rect.top + 50);
            }
        }, 0, 500);
    }

    public static void cancelTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
