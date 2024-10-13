package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.status.PluginManager.loadAllPlugins

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
object PluginInitializer : AbstractInitializer() {
    public override fun exec() {
        try {
            loadAllPlugins()
        } catch (e: Exception) {
            log.warn(e) { "插件加载失败" }
        }
    }

}
