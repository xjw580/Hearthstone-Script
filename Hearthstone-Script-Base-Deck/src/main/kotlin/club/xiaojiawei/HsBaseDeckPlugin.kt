package club.xiaojiawei

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:57
 */
class HsBaseDeckPlugin: DeckPlugin {
    override fun version(): String {
        return "1.0.0"
    }

    override fun author(): String {
        return "XiaoJiawei"
    }

    override fun description(): String {
        return "捆绑，通用策略，未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可"
    }

    override fun id(): String {
        return "xjw-base-plugin"
    }

    override fun name(): String {
        return "基础"
    }

    override fun homeUrl(): String {
        return "https://github.com/xjw580/Hearthstone-Script"
    }
}