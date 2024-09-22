package club.xiaojiawei.data

import lombok.Getter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * 存储application.yaml里的值
 * @author 肖嘉威
 * @date 2023/7/4 18:20
 */
@Component
class SpringData {
    /**
     * 游戏日志配置路径
     */
    @Value("\${game.log.configuration.path}")
    var gameLogConfigurationPath: String? = null

    /**
     * 脚本配置文件路径
     */
    @Value("\${script.configuration.file}")
    var scriptConfigurationFile: String? = null

    @Value("\${script.version}")
    var version: String? = null

    @Value("\${script.path}")
    var scriptPath: String? = null

    @Value("\${script.resource}")
    var resourcePath: String? = null

    @Value("\${game.log.out.path}")
    var gameLogPath: String? = null

    @Value("\${game.log.out.file.screen}")
    var screenLogName: String? = null

    @Value("\${game.log.out.file.power}")
    var powerLogName: String? = null

    @Value("\${game.log.out.file.deck}")
    var deckLogName: String? = null

    @Value("\${spring.main.web-application-type}")
    var webType: String? = null

    @Value("\${server.port}")
    var serverPort: String? = null

}
