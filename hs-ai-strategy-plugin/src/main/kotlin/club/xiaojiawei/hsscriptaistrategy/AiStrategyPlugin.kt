package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptstrategysdk.StrategyPlugin

class AiStrategyPlugin : StrategyPlugin {

    override fun description(): String =
        "基于大语言模型的炉石传说决策策略插件，启用后每回合由LLM分析场面并决定出牌、攻击、英雄技能、结束回合等动作"

    override fun author(): String = "xjw580"

    override fun version(): String = VersionInfo.VERSION

    override fun id(): String = "ai-strategy-plugin-llm"

    override fun name(): String = "AI决策策略插件"

    override fun homeUrl(): String = "https://github.com/xjw580/Hearthstone-Script"

    override fun cardSDKVersion(): String? =
        if (VersionInfo.CARD_SDK_VERSION_USED.endsWith("}")) null else VersionInfo.CARD_SDK_VERSION_USED

    override fun strategySDKVersion(): String? =
        if (VersionInfo.STRATEGY_SDK_VERSION_USED.endsWith("}")) null else VersionInfo.STRATEGY_SDK_VERSION_USED

}
