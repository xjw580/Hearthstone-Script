package club.xiaojiawei.hsscript.consts

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
    val gameLogConfigurationPath: String = ""

    /**
     * 脚本配置文件路径
     */
    @Value("\${script.configuration.file}")
    val scriptConfigurationFile: String = ""

    @Value("\${script.version}")
    val version: String? = null

    @Value("\${script.path}")
    val scriptPath: String? = null

    @Value("\${script.resource}")
    val resourcePath: String? = null

    @Value("\${game.log.out.path}")
    val gameLogPath: String? = null

    @Value("\${game.log.out.file.screen}")
    val screenLogName: String? = null

    @Value("\${game.log.out.file.power}")
    val powerLogName: String? = null

    @Value("\${game.log.out.file.deck}")
    val deckLogName: String? = null

    @Value("\${spring.main.web-application-type}")
    val webType: String? = null

    @Value("\${server.port}")
    val serverPort: String? = null

}
