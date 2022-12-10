package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.entity.TagChangeEntity;
import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listen.PowerFileListen;
import club.xiaojiawei.hearthstone.pool.MyThreadPool;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
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
public class GameOverPhaseStrategy extends PhaseStrategy {

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.GAME_OVER_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        SystemUtil.delayMedium();
        RandomAccessFile accessFile = PowerFileListen.getAccessFile();
//        宣布本局游戏胜者，败者
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length() + 10000){
                    accessFile.seek(accessFile.length());
                }
                break;
            }else if (PowerFileListen.isRelevance(l)){
                PowerFileListen.setMark(System.currentTimeMillis());
                if (l.contains("TAG_CHANGE")){
                    TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    PowerLogUtil.dealTagChange(tagChangeEntity);
                }
            }
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MouseUtil.leftButtonClick((rect.right + rect.left) >> 1, rect.top + 50);
            }
        }, 0, 500);
        log.info(War.getCurrentPhase().getComment() + " -> 结束");
        log.info("已完成第 " + ++War.warCount + " 把游戏");
//        MyThreadPool.reset();
        War.reset();
    }

    private static Timer timer;

    public static void cancelTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
