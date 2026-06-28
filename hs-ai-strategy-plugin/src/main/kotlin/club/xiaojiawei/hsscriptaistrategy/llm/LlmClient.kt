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

        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            log.error { "AI请求失败, status=${response.statusCode()}, body=${response.body()}" }
            throw RuntimeException("AI请求失败, status=${response.statusCode()}")
        }

        val chatResponse = mapper.readValue(response.body(), ChatResponse::class.java)
        val content = chatResponse.choices.firstOrNull()?.message?.content
            ?: throw RuntimeException("AI响应无内容")
        log.info { "AI响应内容: $content" }
        return content
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
