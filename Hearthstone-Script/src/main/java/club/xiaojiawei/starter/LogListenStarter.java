package club.xiaojiawei.starter;

import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.listener.log.AbstractLogListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 * 初始化和启动日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 17:22
 */
@Slf4j
@Component
public class LogListenStarter extends AbstractStarter{

    @Resource
    protected Properties scriptConfiguration;
    @Resource
    protected SpringData springData;
    @Resource
    private AbstractLogListener logListener;

    @Override
    protected void exec() {
        File filePath = new File(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()) + springData.getGameLogPath());
        File [] files;
        if (!filePath.exists() || (files = filePath.listFiles()) == null || files.length == 0){
            log.error("游戏日志目录读取失败");
            return;
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        AbstractLogListener.setLogDir(files[files.length - 1]);
        log.info("游戏日志目录读取成功：" + files[files.length - 1].getAbsoluteFile());
        logListener.listen();
        startNextStarter();
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
