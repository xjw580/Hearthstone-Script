package club.xiaojiawei.hearthstone.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:35
 */
@Slf4j
public class InitUtil {

    private static final String CONFIGURATION_PATH = System.getenv().get("LOCALAPPDATA") + "/Blizzard/Hearthstone/log.config";

    private static final String[] CONFIGURATION_ADDITION = {"[Power]", "LogLevel=1", "FilePrinting=True", "ConsolePrinting=False", "ScreenPrinting=False", "Verbose=True"};

    private static int index = 0;

    /**
     * 打开炉石传说对局日志功能
     */
    public static void openPowerLog(){
        try(BufferedReader reader = new BufferedReader(new FileReader(CONFIGURATION_PATH))) {
            if (!isExist(reader)){
                log.info("炉石传说对局日志未打开");
                try(BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIGURATION_PATH, true))){
                    for (String s : CONFIGURATION_ADDITION) {
                        writer.write(s + "\n");
                    }
                }
                log.info("炉石传说对局日志已打开");
            }
            index = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isExist(BufferedReader reader) throws IOException {
        String s;
        while ((s = reader.readLine()) != null){
            if (s.strip().equals(CONFIGURATION_ADDITION[index])){
                return CONFIGURATION_ADDITION[++index].equals(reader.readLine()) &&
                        CONFIGURATION_ADDITION[++index].equals(reader.readLine()) &&
                        CONFIGURATION_ADDITION[++index].equals(reader.readLine()) &&
                        CONFIGURATION_ADDITION[++index].equals(reader.readLine()) &&
                        CONFIGURATION_ADDITION[++index].equals(reader.readLine());
            }
        }
        return false;
    }

}
