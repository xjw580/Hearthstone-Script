package club.xiaojiawei

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:57
 */
class HsBaseCardPlugin : CardPlugin {

    override fun version(): String {
        return "1.0.0"
    }

    override fun author(): String {
        return "XiaoJiawei"
    }

    override fun description(): String {
        return "捆绑"
    }

    override fun id(): String {
        return "xjw-base-plugin"
    }

    override fun name(): String {
        return "基础"
    }

}