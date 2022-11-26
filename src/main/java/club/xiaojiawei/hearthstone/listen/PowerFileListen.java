package club.xiaojiawei.hearthstone.listen;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:27
 */
@Component
@Slf4j
public class PowerFileListen {

    private static File file;
    private static String suffix;
    private static BufferedReader reader;
    private static long lastChangeTime;

    private static Timer timer;

    public static void cancelTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Value("${game.log.path.power}")
    public void setFile(String suffix){
        PowerFileListen.suffix = suffix;
    }

    public static void listenScreenStatus(){
        try {
            reader = new BufferedReader(new FileReader(file = new File(PROPERTIES.getProperty("gamepath") + suffix)));
            lastChangeTime = file.lastModified();
        } catch (FileNotFoundException e) {
            log.error("未找到LoadingScreen.log文件", e);
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    readPowerLog();
                } catch (IOException e) {
                    log.error(file.getName() + "文件读取失败", e);
                }
            }
        }, 1000);
    }

    private static void readPowerLog() throws IOException {
        String l;
        while ((l = reader.readLine()) != null){

        }
    }

}
