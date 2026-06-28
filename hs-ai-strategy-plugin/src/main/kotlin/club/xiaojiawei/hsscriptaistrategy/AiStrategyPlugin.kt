package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptstrategysdk.StrategyPlugin
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font

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

    override fun graphicDescription(): VBox {
        val box = VBox(6.0)
        val title = Label("AI决策策略配置").apply { font = Font.font(15.0) }

        val enabledCb = CheckBox("启用AI").apply { isSelected = AiConfig.isEnabled() }
        val baseUrlField = TextField(AiConfig.baseUrl()).apply {
            promptText = "https://dashscope.aliyuncs.com/compatible-mode/v1"
            prefWidth = 480.0
        }
        val modelField = TextField(AiConfig.model()).apply {
            promptText = "glm-5.2"
            prefWidth = 480.0
        }
        val apiKeyField = TextField(AiConfig.apiKey()).apply {
            promptText = "sk-xxx"
            prefWidth = 480.0
        }
        val providerField = TextField(AiConfig.provider()).apply {
            promptText = "openai"
            prefWidth = 200.0
        }
        val timeoutField = TextField(AiConfig.timeout().toString()).apply {
            promptText = "30000"
            prefWidth = 100.0
        }
        val saveBtn = Button("保存配置").apply {
            style = "-fx-background-color: #4CAF50; -fx-text-fill: white;"
            setOnAction {
                AiConfig.setEnabled(enabledCb.isSelected)
                AiConfig.setBaseUrl(baseUrlField.text.trim())
                AiConfig.setModel(modelField.text.trim())
                AiConfig.setApiKey(apiKeyField.text.trim())
                AiConfig.setProvider(providerField.text.trim())
                AiConfig.setTimeout(timeoutField.text.trim().toIntOrNull() ?: 30000)
                AiConfig.save()
                text = "已保存 ✓"
            }
        }
        val hint = Label("保存后立即生效。配置写入 config/script.ini [ai] 分组。").apply {
            font = Font.font(11.0)
            style = "-fx-text-fill: gray;"
            isWrapText = true
        }
        box.children.addAll(
            title, enabledCb,
            Label("API地址:"), baseUrlField,
            Label("模型:"), modelField,
            Label("API Key:"), apiKeyField,
            Label("Provider:"), providerField,
            Label("超时(ms, 推理模型建议60000+):"), timeoutField,
            HBox(10.0, saveBtn), hint,
        )
        return box
    }

}
