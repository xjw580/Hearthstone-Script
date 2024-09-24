package club.xiaojiawei.initializer;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
@Component
@Slf4j
public class LogInitializer extends AbstractInitializer{

    @Resource
    private SpringData springData;

    @Resource
    private Properties scriptConfiguration;

    @Override
    public void exec() {
        File logFile = new File(springData.getGameLogConfigurationPath());
        if (!logFile.exists() && logFile.mkdirs() && logFile.delete()){
            log.info(logFile.getName() + "文件父目录创建成功");
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))){
            writer.write("""
                    [LoadingScreen]
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
                    [Power]
                    LogLevel=1
                    FilePrinting=True
                    ConsolePrinting=False
                    ScreenPrinting=False
                    Verbose=True
                    """);
            log.info(logFile.getName() + "文件重写完成，游戏日志已打开，首次重写需要重启炉石传说");
        } catch (IOException e) {
            throw new RuntimeException("文件重写失败，游戏日志未打开，脚本无法运行", e);
        }

        File file = Path.of(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()), "client.config").toFile();
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.warn("", e);
                return;
            }
        }
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
            bufferedWriter.write("""
                    [Log]
                    FileSizeLimit.Int=24576
                    """);
            ScriptStaticData.MAX_LOG_SIZE = 24576 * 1024;
        } catch (IOException e) {
            log.warn("", e);
        }

    }

    @Override
    public int getOrder() {
        return 90;
    }
}
