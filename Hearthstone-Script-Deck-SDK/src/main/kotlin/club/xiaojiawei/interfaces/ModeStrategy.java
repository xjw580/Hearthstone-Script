package club.xiaojiawei.interfaces;

/**
 * @author 肖嘉威
 * @date 2024/9/7 13:50
 */
public interface ModeStrategy<T> {

    void wantEnter();

    void afterLeave();

    void entering();

    void entering(T t);

}
