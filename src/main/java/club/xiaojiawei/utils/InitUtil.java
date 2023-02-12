package club.xiaojiawei.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:35
 */
@Slf4j
public class InitUtil {

    private static final String GAME_CONFIGURATION_PATH;
    private static final String SCRIPT_CONFIGURATION_PATH;
    private static final String SCRIPT_CONFIGURATION_NAME;

    static {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = InitUtil.class.getClassLoader().getResourceAsStream("application.yml");
        if (resourceAsStream != null){
            Map<String, Object> map = yaml.load(resourceAsStream);
            Map<String, Object> gameMap = (Map<String, Object>) map.get("game");
            Map<String, Object> gameConfigMap = (Map<String, Object>) gameMap.get("config");
            Map<String, Object> scriptMap = (Map<String, Object>) map.get("script");
            Map<String, Object> scriptMainMap = (Map<String, Object>) scriptMap.get("main");
            GAME_CONFIGURATION_PATH = System.getenv().get("LOCALAPPDATA") + gameConfigMap.get("path");
            SCRIPT_CONFIGURATION_PATH = (String) scriptMainMap.get("path");
            SCRIPT_CONFIGURATION_NAME = (String) scriptMainMap.get("name");
        }else {
            log.error("spring配置文件未找到");
            GAME_CONFIGURATION_PATH = null;
            SCRIPT_CONFIGURATION_PATH = null;
            SCRIPT_CONFIGURATION_NAME = null;
        }
    }

    public static void init(){
        openPowerLog();
        generateScriptPath();
    }

    @SneakyThrows
    public static void generateScriptPath(){
        File file = new File(SCRIPT_CONFIGURATION_PATH);
        if (!file.exists() && !file.mkdirs()){
            log.error("脚本目录不存在且创建失败，path：" + SCRIPT_CONFIGURATION_PATH);
        }
        File propertiesFile = new File(SCRIPT_CONFIGURATION_PATH + SCRIPT_CONFIGURATION_NAME);
        if (!propertiesFile.exists()){
            try(FileWriter fileWriter = new FileWriter(propertiesFile)){
                fileWriter.write("""
                            date=1,2,3,4,5,6,7
                            time=0-24,0-0,0-0
                            gamepath=C:/ProgramData/hs_script/
                            platformpath=C:/ProgramData/hs_script/
                            mode=TOURNAMENT
                            ranked=CLASSIC
                            deck=ZOO
                            """);
            }
            log.info(propertiesFile.getAbsolutePath() + "创建成功");
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
            log.info("log.config重写完成，已打开游戏日志功能");
        } catch (IOException e) {
            log.error("log.config重写失败，游戏日志功能可能未打开", e);
        }
    }

}
