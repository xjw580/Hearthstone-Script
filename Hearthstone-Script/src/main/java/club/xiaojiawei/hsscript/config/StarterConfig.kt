package club.xiaojiawei.hsscript.config

import club.xiaojiawei.hsscript.starter.*

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
object StarterConfig {

    val starter: AbstractStarter = ClearStarter().also {
        it.setNextStarter(PlatformStarter())
            .setNextStarter(LoginPlatformStarter())
            .setNextStarter(GameStarter())
            .setNextStarter(InjectStarter())
            .setNextStarter(LogListenStarter())
            .setNextStarter(ExceptionListenStarter())
    }

}
