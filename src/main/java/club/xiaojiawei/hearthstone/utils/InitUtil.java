package club.xiaojiawei.hearthstone.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:35
 */
@Slf4j
public class InitUtil {

    private static final String GAME_CONFIGURATION_PATH = System.getenv().get("LOCALAPPDATA") + "/Blizzard/Hearthstone/log.config";
    private static final String SCRIPT_CONFIGURATION_PATH = "C:/ProgramData/hs_script/";
    private static final String SCRIPT_CONFIGURATION_NAME= "game.properties";

    public static void init(){
        openPowerLog();
        generateMainPath();
    }

    @SneakyThrows
    public static void generateMainPath(){
        File file = new File(SCRIPT_CONFIGURATION_PATH);
        if (!file.exists()){
            file.mkdir();
            try(FileWriter fileWriter = new FileWriter(SCRIPT_CONFIGURATION_PATH + SCRIPT_CONFIGURATION_NAME)){
                fileWriter.write("""
                            date=1,2,3,4,5,6,7
                            time=0-24,0-0,0-0
                            gamepath=C:/ProgramData
                            platformpath=C:/ProgramData
                            mode=TOURNAMENT
                            ranked=CLASSIC
                            deck=ZOO
                            """);
            }
        }
    }

    /**
     * 打开炉石传说对局日志功能
     */
    public static void openPowerLog(){
        File file = new File(GAME_CONFIGURATION_PATH);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
            bufferedWriter.write("""
                    [LoadingScreen]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=False
                    [Arena]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=False
                    [Decks]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=False
                    [Achievements]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=False
                    [FullScreenFX]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=False
                    [Power]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=True
                                 
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("log.config重写完成，已打开日志功能");
    }

}
