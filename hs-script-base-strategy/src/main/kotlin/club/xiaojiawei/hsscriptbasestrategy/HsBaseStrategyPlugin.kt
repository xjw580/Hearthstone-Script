package club.xiaojiawei.hsscriptbasestrategy

import club.xiaojiawei.StrategyPlugin

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:57
 */
class HsBaseStrategyPlugin : StrategyPlugin {
    override fun version(): String = VersionInfo.VERSION

    override fun author(): String = "XiaoJiawei"

    override fun description(): String =
        """
        捆绑
        """.trimIndent()

    override fun id(): String = "xjw-base-plugin"

    override fun name(): String = "基础"

    override fun homeUrl(): String = "https://github.com/xjw580/Hearthstone-Script"

    override fun sdkVersion(): String = VersionInfo.SDK_VERSION

}