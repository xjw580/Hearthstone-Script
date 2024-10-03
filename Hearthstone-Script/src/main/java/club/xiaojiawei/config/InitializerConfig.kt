package club.xiaojiawei.config

import club.xiaojiawei.initializer.*

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
object InitializerConfig {

    val starter: AbstractInitializer = BaseInitializer
        .setNextInitializer(ResourceInitializer)
        .setNextInitializer(GamePathInitializer)
        .setNextInitializer(GameLogInitializer)
        .setNextInitializer(PluginInitializer)

}
