package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 游戏结束阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
@Slf4j
@Component
public class GameOverPhaseStrategy extends AbstractPhaseStrategy{

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealChangeEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealOtherThenIsOver(String line) {
        over();
        return true;
    }

    private void over(){
        WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
        if (platformHWND != null){
            User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE);
        }
        War.setMyTurn(false);
        SystemUtil.stopAllThread();
        War.increaseWarCount();
        try {
            SystemUtil.delay(1000);
            RandomAccessFile accessFile = powerLogListener.getAccessFile();
            accessFile.seek(accessFile.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameUtil.clickGameEndPageTask();
    }
}
