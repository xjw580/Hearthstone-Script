package club.xiaojiawei

import club.xiaojiawei.config.PluginScope

/**
 * @author 肖嘉威
 * @date 2024/9/22 19:18
 */
interface CardPlugin : Plugin {

    /**
     * 对哪些插件可见
     * @return 插件id数组
     */
    fun pluginScope(): Array<String> {
//        可自定义范围，如下
//        return arrayOf("id1", "id2", "id3")
        return PluginScope.PROTECTED
    }

}