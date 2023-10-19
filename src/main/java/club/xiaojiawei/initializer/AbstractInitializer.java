package club.xiaojiawei.initializer;

import club.xiaojiawei.config.InitializerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 在 {@link InitializerConfig#initializer()} 添加新的Initializer
 * @author 肖嘉威
 * @date 2023/7/4 11:24
 */
@Slf4j
public abstract class AbstractInitializer {

    protected AbstractInitializer nextInitializer;

    public void init(){
        log.info("执行" + getClass().getSimpleName());
        exec();
    }

    protected abstract void exec();

    public AbstractInitializer setNextInitializer(AbstractInitializer nextInitializer) {
        return this.nextInitializer = nextInitializer;
    }

}
