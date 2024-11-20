package club.xiaojiawei.hsscript.interfaces.closer

/**
 * 使用需要手动调用释放资源
 * @author 肖嘉威
 * @date 2024/11/19 14:27
 */
interface ManuallyReleased {
    /**
     * 释放资源
     */
    fun release()
}