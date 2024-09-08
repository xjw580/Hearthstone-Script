package club.xiaojiawei.initializer;

import club.xiaojiawei.status.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
@Component
@Slf4j
public class PluginInitializer extends AbstractInitializer{

    @Override
    public void exec() {
        try {
            PluginManager.loadAllPlugins();
        } catch (Exception e) {
            log.warn("插件加载失败", e);
        }
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
