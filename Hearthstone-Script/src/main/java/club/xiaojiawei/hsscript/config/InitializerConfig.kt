package club.xiaojiawei.hsscript.config

import club.xiaojiawei.hsscript.initializer.*

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
object InitializerConfig {

    val initializer: AbstractInitializer = BaseInitializer

    init {
        BaseInitializer
            .setNextInitializer(ResourceInitializer)
            .setNextInitializer(GamePathInitializer)
            .setNextInitializer(GameLogInitializer)
            .setNextInitializer(PluginInitializer)
    }

}
