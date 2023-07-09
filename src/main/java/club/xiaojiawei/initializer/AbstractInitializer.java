package club.xiaojiawei.initializer;

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:24
 * @msg
 */
public abstract class AbstractInitializer {

    protected AbstractInitializer nextInitializer;

    public abstract void init();

    public AbstractInitializer setNextInitializer(AbstractInitializer nextInitializer) {
        return this.nextInitializer = nextInitializer;
    }

}
