package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.enums.VersionTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.regex.Pattern
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2023/9/16 20:05
 */
class Release : Comparable<Release> {

    @JsonProperty("tag_name")
    var tagName: String = ""

    @JsonProperty("prerelease")
    var isPreRelease = false

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("body")
    var body: String? = null

    /**
     * @param release the object to be compared.
     * @return
     */
    override fun compareTo(release: Release): Int {
        if (release.tagName.isBlank()) return Int.MAX_VALUE
        val version1: String = this.tagName
        val version2: String = release.tagName
        return compareVersion(version1, version2)
    }

    override fun toString(): String {
        return "Release{" +
                "tagName='$tagName', " +
                "preRelease=$isPreRelease, " +
                "name='$name', " +
                "body='${body?.let { "\n$it".trimIndent() } ?: "null"}'" +
                "}"
    }

    companion object {
        fun compareVersion(version1: String, version2: String): Int {
            val regex = "\\d+(\\.\\d+)*"
            val pattern = Pattern.compile(regex)
            val matcher1 = pattern.matcher(version1)
            val matcher2 = pattern.matcher(version2)
            val isFind1 = matcher1.find()
            val isFind2 = matcher2.find()
            if (!isFind1 || !isFind2) {
                log.warn { String.format("版本号有误，version1：%s，version2：%s", version1, version2) }
                return Int.MAX_VALUE
            }
            val v1 = matcher1.group().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val v2 = matcher2.group().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val minLength = min(v1.size.toDouble(), v2.size.toDouble()).toInt()
            var result = 0
            for (i in 0 until minLength) {
                val s1 = v1[i]
                val s2 = v2[i]
                result = s1.toInt().compareTo(s2.toInt())
                if (result != 0) {
                    return result
                }
            }
            if (v1.size == v2.size) {
                val split1 = version1.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val split2 = version2.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return if (split1.size > 1 && split2.size > 1) {
                    VersionTypeEnum.getEnum(split1[1]).order - VersionTypeEnum.getEnum(split2[1]).order
                } else {
                    split1.size - split2.size
                }
            }
            return v1.size.compareTo(v2.size)
        }
    }
}
