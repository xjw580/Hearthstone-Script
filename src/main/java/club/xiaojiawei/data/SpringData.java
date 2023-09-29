package club.xiaojiawei.data;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 肖嘉威
 * @date 2023/7/4 18:20
 * @msg 存储application.yaml里的值
 */
@Component
@Getter
public class SpringData {
    /**
     * 游戏日志配置路径
     */
    @Value("${game.log.configuration.path}")
    private String gameLogConfigurationPath;
    /**
     * 脚本配置文件路径
     */
    @Value("${script.configuration.file}")
    private String scriptConfigurationFile;
    @Value("${script.version}")
    private String version;
    @Value("${script.path}")
    private String scriptPath;
    @Value("${game.log.out.path}")
    private String gameLogPath;
    @Value("${game.log.out.file.screen}")
    private String screenLogName;
    @Value("${game.log.out.file.power}")
    private String powerLogName;
    @Value("${game.log.out.file.deck}")
    private String deckLogName;
    @Value("${spring.main.web-application-type}")
    private String webType;
    @Value("${server.port}")
    private String serverPort;
}
