package club.xiaojiawei.hearthstone.listener;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

import static club.xiaojiawei.hearthstone.constant.GameMapConst.MODE_MAP;
import static club.xiaojiawei.hearthstone.constant.SystemConst.*;

/**
 * @author 肖嘉威
 * @date 2022/11/24 22:06
 */
@Component
@Slf4j
public class ScreenFileListener {


    private static File file;
    private static BufferedReader reader;
    private static long lastChangeTime;
    private static String suffix;

    @Value("${game.log.path.screen}")
    public void setFile(String suffix){
        ScreenFileListener.suffix = suffix;
    }

    public void init(){
        try {
            File gamepath = new File(PROPERTIES.getProperty("gamepath") + GAME_LOG_PATH_SUFFIX);
            if (!gamepath.exists() && !gamepath.mkdirs()){
                log.error("游戏日志目录不存在且创建失败，path：" + gamepath);
            }
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
            log.info(file.getName() + "读取正常");
        } catch (FileNotFoundException e) {
            log.error("未找到" + file.getName() +  "文件", e);
        }
    }

    private volatile boolean reading;
    @Scheduled(fixedRate=1000, initialDelay = 3000)
    public void listenScreenStatus() {
        if (!reading && lastChangeTime < file.lastModified()){
            lastChangeTime = file.lastModified();
            try {
                readScreenLog();
            } catch (IOException e) {
                log.error(file.getName() + "文件读取失败", e);
            }
        }
    }

    private void readScreenLog() throws IOException {
        reading = true;
        try{
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
                log.warn("游戏被关闭，重启中，请稍等");
                SystemUtil.reStart();
            }
        }finally {
            reading = false;
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
        if (lastMode == null){
            return;
        }
        ModeEnum currentMode = MODE_MAP.getOrDefault(lastMode, ModeEnum.UNKNOWN);
        currentMode.getModeStrategy().get().afterInto();
    }

    @SneakyThrows
    public static void reset(){
        if (reader != null){
            reader.close();
        }
        reader = new BufferedReader(new FileReader(file));
    }
}
