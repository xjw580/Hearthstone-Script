package club.xiaojiawei.initializer;

import club.xiaojiawei.config.InitializerConfig;
import club.xiaojiawei.interfaces.Chain;
import lombok.extern.slf4j.Slf4j;

/**
 * 在 {@link InitializerConfig#initializer()} 添加新的Initializer
 * @author 肖嘉威
 * @date 2023/7/4 11:24
 */
@Slf4j
public abstract class AbstractInitializer implements Chain {

    protected AbstractInitializer nextInitializer;

    public void init(){
        log.info("执行" + getClass().getSimpleName());
        exec();
        initNextInitializer();
    }

    protected abstract void exec();

    public AbstractInitializer setNextInitializer(AbstractInitializer nextInitializer) {
        return this.nextInitializer = nextInitializer;
    }

    protected void initNextInitializer(){
        if (nextInitializer != null){
            nextInitializer.init();
        }
    }

}
