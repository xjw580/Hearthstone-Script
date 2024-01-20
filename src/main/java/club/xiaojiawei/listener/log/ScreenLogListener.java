package club.xiaojiawei.listener.log;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 游戏界面监听器
 * @author 肖嘉威
 * @date 2023/7/5 14:55
 */

@Slf4j
@Component
public class ScreenLogListener extends AbstractLogListener{

    @Resource
    private Core core;

    @Autowired
    public ScreenLogListener(SpringData springData) {
        super(springData.getScreenLogName(), 0, 1500, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void readOldLog() throws IOException {
        String line;
        int index;
        ModeEnum finalMode = null;
        while ((line = accessFile.readLine()) != null) {
            if ((index = line.indexOf("currMode")) != -1) {
                finalMode = ModeEnum.valueOf(line.substring(index + 9));
            }
        }
        Mode.setCurrMode(finalMode);
    }

    @Override
    protected void listenLog() throws IOException, InterruptedException {
        String line;
        while (!isPause.get().get() && Strings.isNotBlank((line = accessFile.readLine()))){
            Mode.setCurrMode(resolveLog(line));
        }
        if (isPause.get().get()){
            cancelListener();
        }
    }

    @Override
    protected void otherListen() {

    }

    @Override
    protected void cancelOtherListener() {

    }

    private ModeEnum resolveLog(String line) throws InterruptedException {
        if (line == null){
            return null;
        }
        int index;
        if ((index = line.indexOf("currMode")) != -1){
            return ModeEnum.valueOf(line.substring(index + 9));
        }else if (line.contains("OnDestroy()")){
            Thread.sleep(2000);
            if (!SystemUtil.isAliveOfGame()){
                log.info("检测到游戏关闭，准备重启游戏");
                core.restart();
            }
        }
        return null;
    }
}
