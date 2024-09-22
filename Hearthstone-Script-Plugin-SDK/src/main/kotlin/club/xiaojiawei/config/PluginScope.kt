package club.xiaojiawei.config

/**
 * 插件可见范围
 * @author 肖嘉威
 * @date 2024/9/22 12:07
 */
object PluginScope {

    /**
     * 对所有其他插件可见
     */
    val PUBLIC = emptyArray<String>()

    /**
     * 仅对相同id插件可见
     */
    val PROTECTED = arrayOf("")

}