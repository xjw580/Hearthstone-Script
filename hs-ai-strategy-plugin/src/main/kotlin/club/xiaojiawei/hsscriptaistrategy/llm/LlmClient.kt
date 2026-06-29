package club.xiaojiawei.hsscriptaistrategy.llm

import club.xiaojiawei.hsscriptaistrategy.config.AiConfig
import club.xiaojiawei.hsscriptbase.config.log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object LlmClient {

    private val mapper = jacksonObjectMapper()

    var lastResponseTime: Long = 0

    private val httpClient: HttpClient by lazy {
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(AiConfig.timeout().toLong()))
            .build()
    }

    fun chat(messages: List<ChatMessage>): String {
        val baseUrl = AiConfig.baseUrl().trimEnd('/')
        if (baseUrl.isEmpty()) {
            throw IllegalStateException("AI_BASE_URL 未配置")
        }
        val url = buildUrl(baseUrl)
        val request = ChatRequest(
            model = AiConfig.model(),
            messages = messages,
            temperature = AiConfig.temperature(),
        )
        val bodyJson = mapper.writeValueAsString(request)
        log.info { "AI请求: url=$url, model=${request.model}, temperature=${request.temperature}" }

        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(AiConfig.timeout().toLong()))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${AiConfig.apiKey()}")
            .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
            .build()

        val start = System.currentTimeMillis()
        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        lastResponseTime = System.currentTimeMillis() - start
        if (response.statusCode() !in 200..299) {
            log.error { "AI请求失败, status=${response.statusCode()}, body=${response.body()}" }
            throw RuntimeException("AI请求失败, status=${response.statusCode()}")
        }

        val chatResponse = mapper.readValue(response.body(), ChatResponse::class.java)
        val content = chatResponse.choices.firstOrNull()?.message?.content
            ?: throw RuntimeException("AI响应无内容")
        log.info { "AI响应(${lastResponseTime}ms): $content" }
        return content
    }

    fun testConnection(): String {
        return try {
            val mockState = """
            {"turn":"my","my_hero":{"name":"猎人","health":30,"armor":0},
            "my_hand":[{"index":0,"name":"测试随从","card_id":"T1","cost":1,"type":"MINION","atk":2,"hp":1,"is_forge":false,"needs_target":false,"is_tradeable":false,"is_choose_one":false,"desc":""},
            {"index":1,"name":"测试法术","card_id":"T2","cost":2,"type":"SPELL","atk":0,"hp":0,"is_forge":false,"needs_target":true,"is_tradeable":false,"is_choose_one":false,"desc":""}],
            "my_board":[{"index":0,"name":"场随从","cost":2,"atk":3,"hp":3,"can_attack":true,"keywords":[],"desc":""}],
            "my_weapon":null,
            "my_hero_power":{"name":"技能","usable":true,"cost":2},
            "my_mana":{"total":3,"available":3,"overload_locked":0},"my_deck_count":20,
            "rival_hero":{"name":"战士","health":30,"armor":0},"rival_hand_count":4,
            "rival_board":[{"index":0,"name":"敌随从","cost":2,"atk":2,"hp":2,"can_attack":false,"keywords":["taunt"],"desc":""}],
            "rival_hero_power":{"name":"技能","usable":true,"cost":2},
            "rival_mana":{"total":3,"available":3}}
            """.trimIndent()
            val messages = listOf(
                ChatMessage("system", "你是炉石传说策略AI。根据场面输出JSON动作数组。"),
                ChatMessage("user", "当前游戏状态：\n$mockState\n请规划本回合动作序列。"),
            )
            val start = System.currentTimeMillis()
            val response = chat(messages)
            val time = System.currentTimeMillis() - start
            "✅ 连通成功 | 耗时: ${time}ms | 响应: ${response.take(50)}..."
        } catch (e: Exception) {
            "❌ 连通失败: ${e.message}"
        }
    }

    fun fetchModels(): List<String> {
        val baseUrl = AiConfig.baseUrl().trimEnd('/')
        if (baseUrl.isEmpty()) return emptyList()
        val url = if (baseUrl.endsWith("/v1")) "$baseUrl/models" else "$baseUrl/v1/models"
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(15000))
            .header("Authorization", "Bearer ${AiConfig.apiKey()}")
            .GET()
            .build()
        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) return emptyList()
        val root = mapper.readTree(response.body())
        val models = mutableListOf<String>()
        root["data"]?.forEach { node ->
            node["id"]?.asText()?.let { models.add(it) }
        }
        return models.sorted()
    }

    fun testModel(model: String): String {
        val baseUrl = AiConfig.baseUrl().trimEnd('/')
        if (baseUrl.isEmpty()) return "❌ URL未配置"
        val url = buildUrl(baseUrl)
        val request = ChatRequest(model = model, messages = listOf(ChatMessage("user", "1")), temperature = 0.0)
        val bodyJson = mapper.writeValueAsString(request)
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(8000))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${AiConfig.apiKey()}")
            .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
            .build()
        val start = System.currentTimeMillis()
        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        val time = System.currentTimeMillis() - start
        return if (response.statusCode() in 200..299) "✅${time}ms" else "❌${response.statusCode()}"
    }

    private fun buildUrl(baseUrl: String): String =
        if (baseUrl.endsWith("/v1")) {
            "$baseUrl/chat/completions"
        } else if (baseUrl.endsWith("/chat/completions")) {
            baseUrl
        } else {
            "$baseUrl/v1/chat/completions"
        }

}
