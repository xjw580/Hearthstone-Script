package club.xiaojiawei.interfaces.closer;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/23 9:09
 */
public interface GameThreadCloser extends Closable{

    void closeGameThread();

    default void close(){
        closeGameThread();
    }

}
