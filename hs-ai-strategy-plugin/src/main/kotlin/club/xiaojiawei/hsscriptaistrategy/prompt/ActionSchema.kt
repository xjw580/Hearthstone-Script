package club.xiaojiawei.hsscriptaistrategy.prompt

import club.xiaojiawei.hsscriptbase.config.log
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class LlmAction(
    val thinking: String = "",
    val action: String = "",
    val cardIndex: Int? = null,
    val attackerIndex: Int? = null,
    val targetIndex: Int? = null,
    val targetSide: String? = null,
    val chooseOneIndex: Int? = null,
)

object ActionParser {

    private val mapper = jacksonObjectMapper()

    fun parseActions(content: String): List<LlmAction>? =
        try {
            val cleaned = cleanJson(content)
            mapper.readValue(cleaned, mapper.typeFactory.constructCollectionType(List::class.java, LlmAction::class.java))
        } catch (e: Exception) {
            log.error { "解析AI动作序列JSON失败: ${e.message}" }
            null
        }

    private fun cleanJson(content: String): String {
        var s = content.trim()
        if (s.startsWith("```")) {
            s = s.removePrefix("```json").removePrefix("```").trim()
            val lastFence = s.lastIndexOf("```")
            if (lastFence >= 0) {
                s = s.substring(0, lastFence).trim()
            }
        }
        val start = s.indexOf('[')
        val end = s.lastIndexOf(']')
        if (start in 0 until end) {
            return s.substring(start, end + 1)
        }
        val objStart = s.indexOf('{')
        val objEnd = s.lastIndexOf('}')
        if (objStart in 0 until objEnd) {
            return "[" + s.substring(objStart, objEnd + 1) + "]"
        }
        return s
    }

}
