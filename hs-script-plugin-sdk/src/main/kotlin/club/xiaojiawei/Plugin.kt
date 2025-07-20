package club.xiaojiawei

import javafx.scene.layout.Pane

/**
 * @author 肖嘉威
 * @date 2024/9/8 16:37
 */
sealed interface Plugin {
    /**
     * 图形化插件描述
     */
    fun graphicDescription(): Pane? = null

    /**
     * 插件描述
     */
    fun description(): String = ""

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
    fun updateUrl(): String = ""

    /**
     * 获取插件信息
     */
    fun getInfoString(): String =
        "name: ${name()}, version: ${version()}, author: ${author()}, id: ${id()}, description: ${description()}"

    /**
     * 使用的sdk版本
     */
    fun sdkVersion(): String

    /**
     * 初始化插件，装载插件前调用
     */
    fun init() {}

}
