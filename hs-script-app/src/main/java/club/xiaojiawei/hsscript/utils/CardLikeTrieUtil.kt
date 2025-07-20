package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.BaseCard
import club.xiaojiawei.bean.Card
import club.xiaojiawei.hsscript.bean.MultiLikeTrieNode
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * @author 肖嘉威
 * @date 2025/1/22 18:38
 */
object CardLikeTrieUtil {

    val root by lazy {
        val node = MultiLikeTrieNode<Method>(true)
        val cardClass = Card::class.java
        val baseCardClass = BaseCard::class.java
        (cardClass.declaredMethods + baseCardClass.declaredMethods).toSet().forEach { method ->
            if (!Modifier.isPublic(method.modifiers)) return@forEach
            for (type in method.parameterTypes) {
                if (type !== Int::class.java && type !== Long::class.java && type !== Boolean::class.java) {
                    return@forEach
                }
            }
            val name = method.name
            if (name.startsWith("set") || name.contains("\$default") || name.contains("clone")) return@forEach
            if (name.startsWith("get")) {
                for (c in name.removePrefix("get")) {
                    node[name.removePrefix("get")] = method
                }
            } else {
                node[name] = method
            }
        }
        node
    }

}