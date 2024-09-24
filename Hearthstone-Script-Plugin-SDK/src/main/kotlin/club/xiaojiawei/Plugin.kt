package club.xiaojiawei

/**
 * @author 肖嘉威
 * @date 2024/9/8 16:37
 */
sealed interface Plugin {

    /**
     * 插件描述
     */
    fun description(): String

    /**
     * 插件作者
     */
    fun author(): String

    /**
     * 插件版本号
     */
    fun version(): String

    /**
     * 插件ID
     */
    fun id(): String

    /**
     * 插件名
     */
    fun name(): String

    /**
     * 插件主页链接
     */
    fun homeUrl(): String

    /**
     * 插件更新链接
     */
    fun updateUrl(): String{
        return ""
    }

    fun getInfoString(): String{
        return "name: ${name()}, version: ${version()}, author: ${author()}, id: ${id()}, description: ${description()}"
    }

}