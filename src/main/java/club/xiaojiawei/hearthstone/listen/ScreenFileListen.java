package club.xiaojiawei.hearthstone.listen;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;

import static club.xiaojiawei.hearthstone.constant.GameConst.MODE_MAP;
import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/24 22:06
 */
@Component
@Slf4j
public class ScreenFileListen {


    private static File file;
    private static BufferedReader reader;
    private static long lastChangeTime;

    @Value("${game.log.path.screen}")
    public void setFile(String suffix){
        try {
            file = new File(PROPERTIES.getProperty("gamepath") + suffix);
            if (!file.exists()){
                try(FileWriter fileWriter = new FileWriter(file)){
                    fileWriter.write("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            reader = new BufferedReader(new FileReader(file));
            lastChangeTime = file.lastModified();
            log.info("LoadingScreen.log 读取正常");
        } catch (FileNotFoundException e) {
            log.error("未找到LoadingScreen.log文件", e);
        }
    }

    @Scheduled(fixedRate=1000, initialDelay = 1000)
    public void listenScreenStatus() {
        if (lastChangeTime < file.lastModified()){
            lastChangeTime = file.lastModified();
            try {
                readScreenLog();
            } catch (IOException e) {
                log.error(file.getName() + "文件读取失败", e);
            }
        }
    }

    private static void readScreenLog() throws IOException {
        String l;
        boolean mark = false;
        while ((l = reader.readLine()) != null){
            mark = false;
            int index;
            if (l.contains("OnDestroy()")){
                mark = true;
                ROBOT.delay(1000);
            }else if (!Core.getPause() && (index = l.indexOf("currMode")) != -1){
                MODE_MAP.getOrDefault(l.substring(index + 9), ModeEnum.UNKNOWN).getModeStrategy().get().afterInto();
            }
        }
        if (mark){
            log.warn("游戏被关闭");
            if (reader != null){
                reader.close();
            }
            reader = new BufferedReader(new FileReader(file));
            Core.openGame();
        }
    }

    public static void initReadScreenLog() throws IOException {
        String l;
        String lastMode = null;
        while ((l = reader.readLine()) != null){
            int index;
            if ((index = l.indexOf("currMode")) != -1){
                lastMode = l.substring(index + 9);
            }
        }
        MODE_MAP.getOrDefault(lastMode, ModeEnum.UNKNOWN).getModeStrategy().get().afterInto();
    }

}
