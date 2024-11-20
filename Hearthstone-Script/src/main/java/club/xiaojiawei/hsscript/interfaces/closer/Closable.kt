package club.xiaojiawei.hsscript.interfaces.closer

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/23 9:10
 */
interface Closable {
    /**
     * 关闭运行，并不代表释放资源
     */
    fun close()
}
