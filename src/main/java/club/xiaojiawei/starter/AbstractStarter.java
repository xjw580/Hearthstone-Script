package club.xiaojiawei.starter;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 * @msg
 */
public abstract class AbstractStarter {

    protected AbstractStarter nextStarter;

    public abstract void start();

    public AbstractStarter setNextStarter(AbstractStarter nextStarter) {
        return this.nextStarter = nextStarter;
    }
}
