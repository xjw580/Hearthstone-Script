package club.xiaojiawei.starter;

import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.listener.AbstractLogListener;
import club.xiaojiawei.listener.DeckLogListener;
import club.xiaojiawei.listener.ScreenLogListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 * @author 肖嘉威
 * @date 2023/9/20 17:22
 * @msg
 */
@Slf4j
@Component
public class ListenStarter extends AbstractStarter{
    @Resource
    protected Properties scriptProperties;
    @Resource
    private ScreenLogListener screenLogListener;
    @Resource
    private DeckLogListener deckLogListener;
    @Resource
    protected SpringData springData;
    @Override
    protected void exec() {
        File filePath = new File(scriptProperties.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey()) + springData.getGameLogPath());
        File [] files;
        if (!filePath.exists() || (files = filePath.listFiles()) == null || files.length == 0){
            log.error("游戏日志目录读取失败");
            return;
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        AbstractLogListener.setLogDir(files[files.length - 1]);
        log.info("游戏日志目录读取成功");
        deckLogListener.listen();
        screenLogListener.listen();
        if (nextStarter != null){
            nextStarter.start();
        }
    }
}
