package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log

/**
 * 在 [InitializerConfig.initializer] 添加新的Initializer
 * @author 肖嘉威
 * @date 2023/7/4 11:24
 */
abstract class AbstractInitializer {

    private var nextInitializer: AbstractInitializer? = null

    fun init() {
        log.info { "执行" + javaClass.simpleName }
        exec()
        initNextInitializer()
    }

    protected abstract fun exec()

    fun setNextInitializer(nextInitializer: AbstractInitializer): AbstractInitializer {
        return nextInitializer.also { this.nextInitializer = it }
    }

    private fun initNextInitializer() {
        nextInitializer?.init()
    }
}
