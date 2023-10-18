package club.xiaojiawei.starter;

import club.xiaojiawei.config.StarterConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 * @msg 在 {@link StarterConfig#starter()} 添加新的Initializer
 */
@Slf4j
public abstract class AbstractStarter {

    protected AbstractStarter nextStarter;

    public void start(){
        log.info("执行" + getClass().getSimpleName());
        exec();
    }

    protected abstract void exec();

    public AbstractStarter setNextStarter(AbstractStarter nextStarter) {
        return this.nextStarter = nextStarter;
    }

    protected void startNextStarter(){
        if (nextStarter != null){
            nextStarter.start();
        }
    }
}
