package club.xiaojiawei.hearthstone.strategy;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
public interface Strategy <T>{

    void afterInto ();

    void afterInto (T t);

}
