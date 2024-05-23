package club.xiaojiawei.closer;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/23 9:08
 */
public interface LogListenerCloser extends Closable {

    void closeLogListener();

    default void close(){
        closeLogListener();
    }

}
