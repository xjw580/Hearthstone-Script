package club.xiaojiawei.starter;

import club.xiaojiawei.config.StarterConfig;
import club.xiaojiawei.interfaces.Chain;
import lombok.extern.slf4j.Slf4j;

/**
 * 在 {@link StarterConfig#starter()} 添加新的Initializer
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 */
@Slf4j
public abstract class AbstractStarter implements Chain {

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
