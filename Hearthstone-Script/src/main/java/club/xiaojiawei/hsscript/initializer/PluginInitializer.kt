package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.status.PluginManager.loadAllPlugins
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
@Component
@Slf4j
object PluginInitializer : AbstractInitializer() {
    public override fun exec() {
        try {
            loadAllPlugins()
        } catch (e: Exception) {
            log.warn(e) { "插件加载失败" }
        }
    }

}
