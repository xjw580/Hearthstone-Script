package club.xiaojiawei.hsscriptaistrategy

import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptaistrategy.llm.LlmClient
import club.xiaojiawei.hsscriptstrategysdk.StrategyPlugin
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

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
        val modelBox = ComboBox<String>().apply {
            isEditable = true
            value = AiConfig.model()
            promptText = "选择或输入模型名"
            prefWidth = 350.0
        }
        val fetchModelBtn = Button("拉取模型").apply {
            style = "-fx-background-color: #FF9800; -fx-text-fill: white;"
            setOnAction {
                text = "拉取中..."
                Thread {
                    try {
                        val models = LlmClient.fetchModels()
                        if (models.isEmpty()) {
                            Platform.runLater { text = "无可用模型"; }
                            return@Thread
                        }
                        val statusMap = ConcurrentHashMap<String, String>()
                        val done = AtomicInteger(0)
                        Platform.runLater {
                            modelBox.items.setAll(models)
                            text = "测试权限 0/${models.size}"
                        }
                        models.forEach { model ->
                            Thread {
                                val result = try { LlmClient.testModel(model) } catch (e: Exception) { "❌超时" }
                                statusMap[model] = result
                                val d = done.incrementAndGet()
                                val currentVal = modelBox.value
                                val items = if (d >= models.size) {
                                    val okModels = models.mapNotNull { m ->
                                        statusMap[m]?.let { m to it }
                                    }.filter { it.second.startsWith("✅") }
                                        .sortedBy { it.second.filter { c -> c.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE }
                                        .map { "${it.first} ${it.second}" }
                                    val failModels = models.mapNotNull { m ->
                                        statusMap[m]?.let { m to it }
                                    }.filter { it.second.startsWith("❌") }
                                        .map { "${it.first} ${it.second}" }
                                    okModels + failModels
                                } else {
                                    models.map { m -> statusMap[m]?.let { "$m $it" } ?: m }
                                }
                                Platform.runLater {
                                    modelBox.items.setAll(items)
                                    modelBox.value = currentVal
                                    text = if (d >= models.size) "拉取模型" else "测试权限 $d/${models.size}"
                                }
                            }.start()
                        }
                    } catch (e: Exception) {
                        Platform.runLater { text = "拉取失败: ${e.message?.take(20)}" }
                    }
                }.start()
            }
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
        val descCb = CheckBox("发送卡牌描述").apply {
            isSelected = AiConfig.includeDesc()
        }
        val descHint = Label("开启后LLM能看到所有卡牌的效果描述，决策更准确但prompt变大、响应变慢。若超时设置较短(如30s)不建议开启，容易超时。").apply {
            font = Font.font(11.0)
            style = "-fx-text-fill: gray;"
            isWrapText = true
            prefWidth = 480.0
        }
        val saveBtn = Button("保存配置").apply {
            style = "-fx-background-color: #4CAF50; -fx-text-fill: white;"
            setOnAction {
                AiConfig.setEnabled(enabledCb.isSelected)
                AiConfig.setBaseUrl(baseUrlField.text.trim())
                AiConfig.setModel((modelBox.value ?: modelBox.editor.text).trim().substringBefore(' '))
                AiConfig.setApiKey(apiKeyField.text.trim())
                AiConfig.setProvider(providerField.text.trim())
                AiConfig.setTimeout(timeoutField.text.trim().toIntOrNull() ?: 30000)
                AiConfig.setIncludeDesc(descCb.isSelected)
                AiConfig.save()
                text = "已保存 ✓"
                Thread {
                    Thread.sleep(1000)
                    Platform.runLater { text = "保存配置" }
                }.start()
            }
        }
        val testResultLabel = Label("")
        val testBtn = Button("测试连通性").apply {
            style = "-fx-background-color: #2196F3; -fx-text-fill: white;"
            setOnAction {
                text = "测试中..."
                testResultLabel.text = ""
                Thread {
                    val result = LlmClient.testConnection()
                    Platform.runLater {
                        testResultLabel.text = result
                        testResultLabel.style = if (result.startsWith("✅")) "-fx-text-fill: green;" else "-fx-text-fill: red;"
                        text = "测试连通性"
                    }
                }.start()
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
            Label("模型:"), HBox(8.0, modelBox, fetchModelBtn),
            Label("API Key:"), apiKeyField,
            Label("Provider:"), providerField,
            Label("超时(ms, 推理模型建议60000+):"), timeoutField,
            descCb, descHint,
            HBox(10.0, saveBtn, testBtn), testResultLabel, hint,
        )
        return box
    }

}
