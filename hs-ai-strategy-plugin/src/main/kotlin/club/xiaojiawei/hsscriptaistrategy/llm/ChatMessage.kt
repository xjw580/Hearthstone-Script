package club.xiaojiawei.hsscriptaistrategy.llm

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatMessage(
    val role: String,
    val content: String,
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double,
    @JsonProperty("stream")
    val stream: Boolean = false,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatResponse(
    val choices: List<Choice> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Choice(
    val message: ChatMessage? = null,
)
